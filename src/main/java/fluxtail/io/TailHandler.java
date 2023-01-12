package fluxtail.io;

public interface TailHandler {
    void accept(char x);

    void exception(Exception exception);
}
