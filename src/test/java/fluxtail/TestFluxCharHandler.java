package fluxtail;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

public class TestFluxCharHandler {
    @Test
    public void testHandle() {
        FluxCharHandler handler = new FluxCharHandler();
        handler.handle('a');
        handler.handle('b');
        handler.handle('c');
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
        handler.handle('a');
        handler.exception(new Exception("message"));
        StepVerifier.create(handler.flux())
                .expectNext('a')
                .expectError()
                .verify();
    }

    @Test
    public void testHandleExceptionHandle() {
        FluxCharHandler handler = new FluxCharHandler();
        handler.handle('a');
        Exception cause = new Exception("message");
        handler.exception(cause);
        Exception exception = catchException(() -> handler.handle('b'));
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasCause(cause);
    }
}
