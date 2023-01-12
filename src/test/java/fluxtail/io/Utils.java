package fluxtail.io;

import patiently.Patiently;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static void rmFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    public static void writeToFile(Path path, String text) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(text);
        }
    }

    public static void appendToFile(Path path, String text) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
            writer.write(text);
        }
    }

    public static List<Character> toCharList(String string) {
        return string.chars().mapToObj(x -> (char) x).collect(Collectors.toList());
    }
}
