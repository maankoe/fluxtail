package fluxtail;

import fluxtail.io.TailHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


public class CharFluxTail implements TailHandler, Fluxable<Character> {
    private final Sinks.Many<Character> characters;
    private Exception handledException;

    public CharFluxTail() {
        this.characters = Sinks
                .many()
                .unicast()
                .onBackpressureBuffer();
    }

    @Override
    public void accept(char x) {
        Sinks.EmitResult result = this.characters.tryEmitNext(x);
        if (result.isFailure()) {
            throw new IllegalStateException("This flux is in a bad state", this.handledException);
        }
    }

    @Override
    public void exception(Exception exception) {
        this.handledException = exception;
        this.characters.tryEmitError(exception);
    }

    public Flux<Character> flux() {
        return characters.asFlux();
    }
}
