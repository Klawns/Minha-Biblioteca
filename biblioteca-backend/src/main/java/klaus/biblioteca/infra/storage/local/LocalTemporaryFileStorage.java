package klaus.biblioteca.infra.storage.local;

import klaus.biblioteca.application.port.TemporaryFileStorage;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalTemporaryFileStorage implements TemporaryFileStorage {
    private final Path directory;

    public LocalTemporaryFileStorage() {
        this(Path.of("temp", "book"));
    }

    public LocalTemporaryFileStorage(Path directory) {
        this.directory = directory;
    }

    @Override
    public String create(InputStream inputStream, String prefix, String suffix) {
        Path path = null;

        try {
            Files.createDirectories(directory);
            path = Files.createTempFile(directory, prefix, suffix);

            try (OutputStream outputStream = Files.newOutputStream(path)) {
                inputStream.transferTo(outputStream);
            }

            return path.toAbsolutePath().toString();
        } catch (IOException exception) {
            if (path != null) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw new IllegalStateException("Could not create temporary file", exception);
        }
    }

    @Override
    public void delete(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not delete temporary file", exception);
        }
    }
}
