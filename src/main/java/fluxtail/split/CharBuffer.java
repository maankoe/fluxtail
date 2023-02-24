package fluxtail.split;

public interface CharBuffer {
    void add(char x);

    boolean isSplit();

    CharSequence read();

    void clear();
}
