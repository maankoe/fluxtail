package fluxtail.io;

import java.util.ArrayList;
import java.util.List;

public class DummyCharHandler implements TailHandler  {
    private final List<Character> characters = new ArrayList<>();
    private final List<Exception> exceptions = new ArrayList<>();

    @Override
    public void accept(char x) {
        this.characters().add(x);
    }

    @Override
    public void exception(Exception exception) {
        this.exceptions.add(exception);
    }

    public synchronized List<Character> characters() {
        return this.characters;
    }

    public synchronized List<Exception> exceptions() {
        return this.exceptions;
    }
}
