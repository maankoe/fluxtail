package fluxtail;

import fluxtail.flux.StringFluxTail;
import fluxtail.split.CharBuffer;
import fluxtail.split.SplitType;
import fluxtail.split.CharSplitCharBuffer;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

public class TestStringFluxTail {

    private static final char DOT = '.';

    @Test
    public void testHandleExclusive() {
        StringFluxTail handler = new StringFluxTail(dotSplittingBuffer(SplitType.EXCLUSIVE));
        handler.accept('a');
        handler.accept('b');
        handler.accept(DOT);
        handler.accept('c');
        handler.accept(DOT);
        StepVerifier.create(handler.flux())
                .expectNext("ab", "c")
                .thenCancel()
                .verify();
    }

    @Test
    public void testHandleInclusive() {
        StringFluxTail handler = new StringFluxTail(dotSplittingBuffer(SplitType.INCLUSIVE));
        handler.accept('a');
        handler.accept('b');
        handler.accept(DOT);
        handler.accept('c');
        handler.accept(DOT);
        StepVerifier.create(handler.flux())
                .expectNext("ab.", "c.")
                .thenCancel()
                .verify();
    }

    @Test
    public void testHandleNoSplitOnEnd() {
        StringFluxTail handler = new StringFluxTail(dotSplittingBuffer(SplitType.INCLUSIVE));
        handler.accept('a');
        handler.accept('b');
        handler.accept('.');
        handler.accept('c');
        StepVerifier.create(handler.flux())
                .expectNext("ab.")
                .thenCancel()
                .verify();
    }

    @Test
    public void testException() {
        StringFluxTail handler = new StringFluxTail(dotSplittingBuffer(SplitType.EXCLUSIVE));
        handler.exception(new Exception("message"));
        StepVerifier.create(handler.flux())
                .expectError()
                .verify();
    }

    @Test
    public void testHandleException() {
        StringFluxTail handler = new StringFluxTail(dotSplittingBuffer(SplitType.EXCLUSIVE));
        handler.accept('a');
        handler.accept('.');
        handler.exception(new Exception("message"));
        StepVerifier.create(handler.flux())
                .expectNext("a")
                .expectError()
                .verify();
    }

    @Test
    public void testHandleExceptionHandle() {
        StringFluxTail handler = new StringFluxTail(dotSplittingBuffer(SplitType.EXCLUSIVE));
        handler.accept('a');
        Exception cause = new Exception("message");
        handler.exception(cause);
        Exception exception = catchException(() -> handler.accept('.'));
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasCause(cause);
    }

    private Supplier<CharBuffer> dotSplittingBuffer(SplitType splitType) {
        return () -> new CharSplitCharBuffer(DOT, splitType);
    }
}
