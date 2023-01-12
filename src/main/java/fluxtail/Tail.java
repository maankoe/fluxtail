package fluxtail;

import fluxtail.flux.CharFluxTail;
import fluxtail.flux.FluxTail;
import fluxtail.flux.StringFluxTail;
import fluxtail.io.TailReader;
import fluxtail.parse.Parser;
import fluxtail.split.Splitter;
import reactor.core.publisher.Flux;

import java.nio.file.Path;

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

    public Flux<CharSequence> read(Splitter splitter) {
        StringFluxTail fluxTail = new StringFluxTail(splitter);
        TailReader reader = new TailReader(this.path, fluxTail, this.sleepMs);
        reader.start();
        return fluxTail.flux();
    }

    public <T> Flux<T> read(Splitter splitter, Parser<T> parser) {
        FluxTail<T> fluxTail = new FluxTail<T>(splitter, parser);
        TailReader reader = new TailReader(this.path, fluxTail, this.sleepMs);
        reader.start();
        return fluxTail.flux();
    }

}
