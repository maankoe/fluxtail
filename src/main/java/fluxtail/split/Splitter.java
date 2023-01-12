package fluxtail.split;

public interface Splitter {
    boolean isSplit(CharBuffer buffer);

    CharBuffer newBuffer();
}
