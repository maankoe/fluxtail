package fluxtail.io;

import org.junit.jupiter.api.BeforeEach;
import patiently.Patiently;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static fluxtail.io.Utils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestTailReader {

    private final int testRetryIntervalMs = 10;
    private final int testRetryMaxMs = 2000;
    private final int tailReaderPollInterval = 10;
    private final Path testPath = Path.of("/tmp/test");

    @BeforeEach
    public void setUp() throws IOException {
        rmFile(this.testPath);
    }

    @Test
    public void testRead() throws Exception {
        String fileContents = "abc";
        writeToFile(this.testPath, fileContents);
        DummyHandler handler = new DummyHandler();
        TailReader reader = new TailReader(this.testPath, handler, tailReaderPollInterval);
        reader.start();
        Patiently.retry(() ->
                assertThat(handler.characters())
                        .containsExactlyElementsOf(toCharList(fileContents))
        ).every(testRetryIntervalMs).until(testRetryMaxMs);
        reader.stop();
    }

    @Test
    public void testReadAppended() throws Exception {
        new GeneralTest(
                this.testPath, this.tailReaderPollInterval, this.tailReaderPollInterval, this.testRetryMaxMs
        ).testTailReaderWithUpdate("abc", "def");
    }

    @Test
    public void testReadFromEmpty() throws Exception {
        new GeneralTest(
                this.testPath, this.tailReaderPollInterval, this.tailReaderPollInterval, this.testRetryMaxMs
        ).testTailReaderWithUpdate("", "abc");
    }

    @Test
    public void testReadNothingInUpdate() throws Exception {
        new GeneralTest(
                this.testPath, this.tailReaderPollInterval, this.tailReaderPollInterval, this.testRetryMaxMs
        ).testTailReaderWithUpdate("abc", "");
    }

    @Test
    public void testReadNothing() throws Exception {
        new GeneralTest(
                this.testPath, this.tailReaderPollInterval, this.tailReaderPollInterval, this.testRetryMaxMs
        ).testTailReaderWithUpdate("", "");
    }

    @Test
    public void testPathRotationSmaller() throws Exception {
        new GeneralTest(
                this.testPath, this.tailReaderPollInterval, this.tailReaderPollInterval, this.testRetryMaxMs
        ).testTailReaderWithOverwrite("abc", "de");
    }

    @Test
    public void testPathRotationSameSize() throws Exception {
        new GeneralTest(
                this.testPath, this.tailReaderPollInterval, this.tailReaderPollInterval, this.testRetryMaxMs
        ). testTailReaderWithOverwrite("abc", "def");
    }

    @Test
    public void testFileGetsRemoved() throws Exception {
        writeToFile(this.testPath, "abc");
        DummyHandler handler = new DummyHandler();
        TailReader reader = new TailReader(this.testPath, handler, tailReaderPollInterval);
        reader.start();
        writeToFile(this.testPath, "some more data");
        rmFile(this.testPath);
        Patiently.retry(() ->
                assertThat(handler.exceptions())
                        .hasSize(1)
                        .allMatch(x -> x instanceof NoSuchFileException)
        ).every(testRetryIntervalMs).until(testRetryMaxMs);
    }

    @Test
    public void testFileDoesNotExist() {
        DummyHandler handler = new DummyHandler();
        TailReader reader = new TailReader(this.testPath, handler, tailReaderPollInterval);
        reader.start();
        Patiently.retry(() ->
                assertThat(handler.exceptions())
                        .hasSize(1)
                        .allMatch(x -> x instanceof NoSuchFileException)
        ).every(testRetryIntervalMs).until(testRetryMaxMs);
    }
}

