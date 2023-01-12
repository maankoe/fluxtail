package fluxtail.io;

import fluxtail.TailHandler;

import java.io.IOException;
import java.nio.file.Path;

public class TailReader {
    private final Path path;
    private final TailHandler handler;
    private final long sleepMs;
    private boolean running;

    public TailReader(Path path, TailHandler handler, long sleepMs) {
        this.path = path;
        this.handler = handler;
        this.sleepMs = sleepMs;
        this.running = false;
    }

    public void start() {
        this.running = true;
        new Thread(this::tail).start();
    }

    public void stop() {
        this.running = false;
    }

    private void tail() {
        try (RABReader reader = new RABReader(this.path)) {
            while (this.running) {
                if (reader.rotated()) {
                    read(reader);
                    reader.reset();
                }
                read(reader);
                Thread.sleep(this.sleepMs);
            }
        } catch (IOException | InterruptedException e) {
            this.handler.exception(e);
        }
    }

    private void read(RABReader reader) throws IOException {
        int x = reader.read();
        while (x >= 0) {
            this.handler.handle((char) x);
            x = reader.read();
        }
    }
}
