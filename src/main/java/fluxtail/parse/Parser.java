package fluxtail.parse;

public interface Parser<T> {
    T parse(String x);
}
