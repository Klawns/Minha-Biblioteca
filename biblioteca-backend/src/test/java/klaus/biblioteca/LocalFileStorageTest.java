package klaus.biblioteca;

import klaus.biblioteca.application.model.StoredFile;
import klaus.biblioteca.infra.storage.local.LocalFileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalFileStorageTest {
    @Test
    void storesRetrievesAndDeletesFile(@TempDir Path directory) throws Exception {
        LocalFileStorage storage = new LocalFileStorage(directory.resolve("storage"));
        byte[] content = "cover".getBytes(StandardCharsets.UTF_8);

        StoredFile first = storage.store(new ByteArrayInputStream(content), "livro.png");
        StoredFile second = storage.store(new ByteArrayInputStream(content), "livro.png");

        assertTrue(Files.isDirectory(directory.resolve("storage")));
        assertTrue(first.urlPath().endsWith(".png"));
        assertNotEquals(first.urlPath(), second.urlPath());
        try (var input = storage.retrieve(first.urlPath())) {
            assertArrayEquals(content, input.readAllBytes());
        }
        storage.delete(first.urlPath());
        storage.delete(first.urlPath());
        assertFalse(Files.exists(Path.of(first.urlPath())));
    }

    @Test
    void readsLegacyRelativeAndCanonicalBookPaths(@TempDir Path directory) throws Exception {
        Path storageDirectory = directory.resolve("storage");
        Files.createDirectories(storageDirectory);
        Files.writeString(storageDirectory.resolve("legacy.png"), "legacy");
        Files.createDirectories(storageDirectory.resolve("books/abc"));
        Files.writeString(storageDirectory.resolve("books/abc/cover"), "canonical");
        LocalFileStorage storage = new LocalFileStorage(storageDirectory);

        try (var legacy = storage.retrieve("legacy.png"); var canonical = storage.retrieve("/books/abc/cover")) {
            assertArrayEquals("legacy".getBytes(StandardCharsets.UTF_8), legacy.readAllBytes());
            assertArrayEquals("canonical".getBytes(StandardCharsets.UTF_8), canonical.readAllBytes());
        }
    }

    @Test
    void reportsMissingMedia() {
        LocalFileStorage storage = new LocalFileStorage(Path.of("missing-storage"));
        assertThrows(RuntimeException.class, () -> storage.retrieve("missing.png"));
    }
}
