package fluxtail.split;

import java.util.LinkedList;


public class CharSplitCharBuffer implements CharBuffer {
    private final LinkedList<Character> chars = new LinkedList<>();
    private final char splitChar;
    private final SplitType splitType;

    public CharSplitCharBuffer(char splitChar, SplitType splitType) {
        this.splitChar = splitChar;
        this.splitType = splitType;
    }

    @Override
    public void add(char x) {
        chars.add(x);
    }

    @Override
    public boolean isSplit() {
        return this.chars.getLast() == this.splitChar;
    }

    @Override
    public CharSequence read() {
        StringBuilder builder = new StringBuilder(this.chars.size());
        for (char x : this.chars) {
            builder.append(x);
        }
        if (this.splitType != SplitType.INCLUSIVE_POST) {
            builder.deleteCharAt(builder.length()-1);
        }
        return builder.toString();
    }

    @Override
    public void clear() {
        if (this.splitType == SplitType.INCLUSIVE_PRE) {
            char inclusiveChar = this.chars.getLast();
            this.chars.clear();
            this.add(inclusiveChar);
        } else {
            this.chars.clear();
        }
    }
}
