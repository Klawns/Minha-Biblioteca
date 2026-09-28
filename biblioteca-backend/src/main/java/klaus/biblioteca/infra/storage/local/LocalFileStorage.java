package klaus.biblioteca.infra.storage.local;

import klaus.biblioteca.application.model.StoredFile;
import klaus.biblioteca.application.port.FileStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;
import java.util.Objects;

public class LocalFileStorage implements FileStorage {
    private final Path directory;

    public LocalFileStorage() {
        this(Path.of("storage"));
    }

    public LocalFileStorage(Path directory) {
        this.directory = Objects.requireNonNull(directory);
    }

    @Override
    public StoredFile store(InputStream inputStream, String fileName) {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(fileName);

        Path storedPath = null;
        try {
            Files.createDirectories(directory);
            if (fileName.startsWith("/books/")) {
                storedPath = directory.resolve(fileName.substring(1));
                Files.createDirectories(storedPath.getParent());
                Files.deleteIfExists(storedPath);
                Files.createFile(storedPath);
            } else {
                storedPath = Files.createTempFile(directory, temporaryPrefix(fileName), extension(fileName));
            }
            try (OutputStream outputStream = Files.newOutputStream(storedPath)) {
                inputStream.transferTo(outputStream);
            }
            return new StoredFile(fileName.startsWith("/books/") ? fileName : storedPath.toAbsolutePath().toString());
        } catch (IOException | RuntimeException exception) {
            if (storedPath != null) {
                try {
                    Files.deleteIfExists(storedPath);
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw new IllegalStateException("Could not store file", exception);
        }
    }

    @Override
    public InputStream retrieve(String path) {
        try {
            return Files.newInputStream(resolve(path));
        } catch (NoSuchFileException exception) {
            throw new MediaFileNotFoundException(path, exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not retrieve file", exception);
        }
    }

    @Override
    public void delete(String path) {
        try {
            Files.deleteIfExists(resolve(path));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not delete file", exception);
        }
    }

    private Path resolve(String path) {
        Objects.requireNonNull(path);
        Path candidate = Path.of(path);
        if (candidate.isAbsolute()) return candidate;
        if (path.startsWith("/books/")) return directory.resolve(path.substring(1));
        // Existing records may contain either a bare storage-relative path or storage/...
        if (path.startsWith("storage/")) return candidate;
        return directory.resolve(path);
    }

    private String extension(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        return extensionIndex >= 0 ? fileName.substring(extensionIndex) : "";
    }

    private String temporaryPrefix(String fileName) {
        String baseName = fileName;
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex > 0) {
            baseName = fileName.substring(0, extensionIndex);
        }
        baseName = baseName.replaceAll("[^a-zA-Z0-9_-]", "_");
        return (baseName.length() >= 3 ? baseName : "file") + "-";
    }
}
