package fluxtail.flux;

import fluxtail.io.TailHandler;
import fluxtail.split.CharBuffer;
import fluxtail.split.Splitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class StringFluxTail implements TailHandler, Fluxable<CharSequence> {
    private CharBuffer buffer;
    private final Splitter splitter;

    private final Sinks.Many<CharSequence> sink;
    private Exception handledException;

    public StringFluxTail(Splitter splitter) {
        this.splitter = splitter;
        this.buffer = this.splitter.newBuffer();
        this.sink = Sinks
                .many()
                .unicast()
                .onBackpressureBuffer();
    }

    @Override
    public void accept(char x) {
        this.buffer.add(x);
        if (splitter.isSplit(this.buffer)) {
            Sinks.EmitResult result = this.sink.tryEmitNext(this.buffer.read());
            if (result.isFailure()) {
                throw new IllegalStateException("This flux is in a bad state", this.handledException);
            }
            this.buffer = this.splitter.newBuffer();
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
