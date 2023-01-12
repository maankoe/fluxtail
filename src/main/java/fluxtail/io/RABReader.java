package fluxtail.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class RABReader implements AutoCloseable {
    private final Path path;

    private RandomAccessFile raf;
    private BufferedReader reader;
    private long pos;
    private long fileTime;

    public RABReader(Path path) throws IOException {
        this.path = path;
        this.fileTime = this.currentFileTime();
        this.init();
    }

    private void init() throws IOException {
        this.raf = new RandomAccessFile(path.toFile(), "r");
        this.reader = new BufferedReader(new FileReader(raf.getFD()));
        pos = 0;
    }

    public void reset() throws IOException {
        this.raf.close();
        this.reader.close();
        this.init();
    }

    public int read() throws IOException {
        int x = this.reader.read();
        pos = this.raf.getFilePointer();
        this.fileTime = this.currentFileTime();
        return x;
    }

    public boolean rotated() throws IOException {
        long length = Files.size(this.path);
        return this.pos > length || (this.pos == length && this.fileTime < this.currentFileTime());
    }

    private long currentFileTime() throws IOException {
        return Files.getLastModifiedTime(this.path).toMillis();
    }

    @Override
    public void close() throws IOException {
        this.raf.close();
        this.reader.close();
    }
}
