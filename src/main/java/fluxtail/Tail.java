package fluxtail;

import fluxtail.flux.CharFluxTail;
import fluxtail.flux.FluxTail;
import fluxtail.flux.StringFluxTail;
import fluxtail.io.TailReader;
import fluxtail.parse.Parser;
import fluxtail.split.CharBuffer;
import fluxtail.split.CharSplitCharBuffer;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.util.function.Supplier;

public class Tail {
    Path path;
    long sleepMs;

    public Tail(Path path, long sleepMs) {
        this.path = path;
        this.sleepMs = sleepMs;
    }

    public Flux<Character> read() {
        CharFluxTail fluxTail = new CharFluxTail();
        TailReader reader = new TailReader(this.path, fluxTail, this.sleepMs);
        reader.start();
        return fluxTail.flux();
    }

    public Flux<CharSequence> read(Supplier<CharBuffer> bufferFactory) {
        StringFluxTail fluxTail = new StringFluxTail(bufferFactory);
        TailReader reader = new TailReader(this.path, fluxTail, this.sleepMs);
        reader.start();
        return fluxTail.flux();
    }

    public <T> Flux<T> read(Supplier<CharBuffer> bufferFactory, Parser<T> parser) {
        FluxTail<T> fluxTail = new FluxTail<>(bufferFactory, parser);
        TailReader reader = new TailReader(this.path, fluxTail, this.sleepMs);
        reader.start();
        return fluxTail.flux();
    }
}
