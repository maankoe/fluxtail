package fluxtail;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

public class TestFluxCharHandler {
    @Test
    public void testHandle() {
        FluxCharHandler handler = new FluxCharHandler();
        handler.accept('a');
        handler.accept('b');
        handler.accept('c');
        StepVerifier.create(handler.flux())
                .expectNext('a', 'b', 'c')
                .thenCancel()
                .verify();
    }

    @Test
    public void testException() {
        FluxCharHandler handler = new FluxCharHandler();
        handler.exception(new Exception("message"));
        StepVerifier.create(handler.flux())
                .expectError()
                .verify();
    }

    @Test
    public void testHandleException() {
        FluxCharHandler handler = new FluxCharHandler();
        handler.accept('a');
        handler.exception(new Exception("message"));
        StepVerifier.create(handler.flux())
                .expectNext('a')
                .expectError()
                .verify();
    }

    @Test
    public void testHandleExceptionHandle() {
        FluxCharHandler handler = new FluxCharHandler();
        handler.accept('a');
        Exception cause = new Exception("message");
        handler.exception(cause);
        Exception exception = catchException(() -> handler.accept('b'));
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasCause(cause);
    }
}
