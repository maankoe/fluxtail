package fluxtail;

import fluxtail.io.TailHandler;
import fluxtail.parse.Parser;
import fluxtail.split.CharBuffer;
import fluxtail.split.Splitter;
import reactor.core.publisher.Sinks;

public class FluxTail<T> implements TailHandler {
    private CharBuffer buffer;
    private final Splitter splitter;
    private final Parser<T> parser;

    private final Sinks.Many<T> sink;
    private Exception handledException;

    public FluxTail(Splitter splitter, Parser<T> parser) {
        this.splitter = splitter;
        this.buffer = this.splitter.newBuffer();
        this.parser = parser;
        this.sink = Sinks
                .many()
                .unicast()
                .onBackpressureBuffer();
    }

    @Override
    public void accept(char x) {
        this.buffer.add(x);
        if (splitter.isSplit(this.buffer)) {
            Sinks.EmitResult result = this.sink.tryEmitNext(this.parser.parse(this.buffer.toString()));
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
}
