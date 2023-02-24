package fluxtail;

import fluxtail.flux.FluxTail;
import fluxtail.flux.StringFluxTail;
import fluxtail.parse.Parser;
import fluxtail.split.CharBuffer;
import fluxtail.split.SplitType;
import fluxtail.split.CharSplitCharBuffer;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

public class TestFluxTail {

    private static final char DOT = '.';

    static class DigitSumParer implements Parser<Integer> {
        @Override
        public Integer apply(CharSequence s) {
            int tot = 0;
            for (char x : s.toString().toCharArray()) {
                tot += Integer.parseInt(Character.toString(x));
            }
            return tot;
        }
    }

    @Test
    public void testHandle() {
        FluxTail<Integer> handler = new FluxTail<>(dotSplittingBuffer(SplitType.EXCLUSIVE), new DigitSumParer());
        handler.accept('1');
        handler.accept('2');
        handler.accept(DOT);
        handler.accept('5');
        handler.accept('7');
        handler.accept(DOT);
        StepVerifier.create(handler.flux())
                .expectNext(3, 12)
                .thenCancel()
                .verify();
    }

    @Test
    public void testException() {
        FluxTail<Integer> handler = new FluxTail<>(dotSplittingBuffer(SplitType.EXCLUSIVE), new DigitSumParer());
        handler.exception(new Exception("message"));
        StepVerifier.create(handler.flux())
                .expectError()
                .verify();
    }

    @Test
    public void testHandleException() {
        FluxTail<Integer> handler = new FluxTail<>(dotSplittingBuffer(SplitType.EXCLUSIVE), new DigitSumParer());
        handler.accept('4');
        handler.accept('.');
        handler.exception(new Exception("message"));
        StepVerifier.create(handler.flux())
                .expectNext(4)
                .expectError()
                .verify();
    }

    @Test
    public void testHandleExceptionHandle() {
        StringFluxTail handler = new StringFluxTail(dotSplittingBuffer(SplitType.EXCLUSIVE));
        handler.accept('7');
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
