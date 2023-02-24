package fluxtail.flux;

import fluxtail.io.TailHandler;
import fluxtail.split.CharBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

public class StringFluxTail implements TailHandler, Fluxable<CharSequence> {
    private final CharBuffer buffer;
    private final Sinks.Many<CharSequence> sink;
    private Exception handledException;

    public StringFluxTail(Supplier<CharBuffer> bufferFactory) {
        this.buffer = bufferFactory.get();
        this.sink = Sinks
                .many()
                .unicast()
                .onBackpressureBuffer();
    }

    @Override
    public void accept(char x) {
        this.buffer.add(x);
        if (this.buffer.isSplit()) {
            Sinks.EmitResult result = this.sink.tryEmitNext(this.buffer.read());
            if (result.isFailure()) {
                throw new IllegalStateException("This flux is in a bad state", this.handledException);
            }
            this.buffer.clear();
        }
    }

    @Override
    public void exception(Exception exception) {
        this.handledException = exception;
        this.sink.tryEmitError(exception);
    }

    @Override
    public Flux<CharSequence> flux() {
        return this.sink.asFlux();
    }
}
