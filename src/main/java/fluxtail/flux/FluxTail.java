package fluxtail.flux;

import fluxtail.io.TailHandler;
import fluxtail.parse.Parser;
import fluxtail.split.CharBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

public class FluxTail<T> implements TailHandler, Fluxable<T> {
    private final CharBuffer buffer;
    private final Parser<T> parser;

    private final Sinks.Many<T> sink;
    private Exception handledException;

    public FluxTail(Supplier<CharBuffer> bufferFactory, Parser<T> parser) {
        this.buffer = bufferFactory.get();
        this.parser = parser;
        this.sink = Sinks
                .many()
                .unicast()
                .onBackpressureBuffer();
    }

    @Override
    public void accept(char x) {
        this.buffer.add(x);
        if (this.buffer.isSplit()) {
            Sinks.EmitResult result = this.sink.tryEmitNext(this.parser.apply(this.buffer.read()));
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
    public Flux<T> flux() {
        return this.sink.asFlux();
    }
}
