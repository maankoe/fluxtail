package fluxtail.parse;

import java.util.function.Function;

public interface Parser<T> extends Function<CharSequence, T> {}
