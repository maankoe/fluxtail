package fluxtail;

import reactor.core.publisher.Flux;

public interface Fluxable<T> {
    Flux<T> flux();
}
