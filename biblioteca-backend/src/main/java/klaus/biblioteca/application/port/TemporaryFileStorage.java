package klaus.biblioteca.application.port;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface TemporaryFileStorage {
    String create(InputStream inputStream, String prefix, String suffix);

    default InputStream open(String path) {
        try {
            return Files.newInputStream(Path.of(path));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not open temporary file", exception);
        }
    }

    void delete(String path);
}
