package fluxtail.io;

import patiently.Patiently;

import java.io.IOException;
import java.nio.file.Path;

import static fluxtail.io.Utils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class GeneralTest {
    private final Path testPath;
    private final long tailReaderPollInterval;
    private final long testRetryIntervalMs;
    private final long testRetryMaxMs;

    public GeneralTest(
            Path testPath,
            long tailReaderPollInterval,
            long testRetryIntervalMs,
            long testRetryMaxMs
    ) {
        this.testPath = testPath;
        this.tailReaderPollInterval = tailReaderPollInterval;
        this.testRetryIntervalMs = testRetryIntervalMs;
        this.testRetryMaxMs = testRetryMaxMs;
    }

    public void testTailReaderWithUpdate(
            String startFileContents, String updateFileContents
    ) throws Exception {
        testTailRreader(startFileContents, updateFileContents, false);
    }

    public void testTailReaderWithOverwrite(
            String startFileContents, String updateFileContents
    ) throws Exception {
        testTailRreader(startFileContents, updateFileContents, true);
    }

    public void testTailRreader(
            String startFileContents, String updateFileContents, boolean overwrite
    ) throws IOException {
        writeToFile(this.testPath, startFileContents);
        DummyCharHandler handler = new DummyCharHandler();
        TailReader reader = new TailReader(this.testPath, handler, tailReaderPollInterval);
        reader.start();
        Patiently.retry(() ->
                assertThat(handler.characters())
                        .containsExactlyElementsOf(toCharList(startFileContents))
        ).every(testRetryIntervalMs).until(testRetryMaxMs);
        if (overwrite) {
            writeToFile(this.testPath, updateFileContents);
        } else {
            appendToFile(this.testPath, updateFileContents);
        }
        Patiently.retry(() ->
                assertThat(handler.characters())
                        .containsExactlyElementsOf(
                                toCharList(startFileContents + updateFileContents)
                        )
        ).every(testRetryIntervalMs).until(testRetryMaxMs);
        reader.stop();
    }
}
