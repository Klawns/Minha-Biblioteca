package klaus.biblioteca;

import klaus.biblioteca.application.model.StoredFile;
import klaus.biblioteca.application.port.FileStorage;
import klaus.biblioteca.application.usecase.StoreCover;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoreCoverTest {
    @Test
    void storesPngNameAndClosesItsStream(@TempDir Path directory) throws Exception {
        Path cover = directory.resolve("generated.png");
        Files.write(cover, new byte[]{1, 2, 3});
        TrackingStorage storage = new TrackingStorage();

        StoredFile result = new StoreCover(storage).execute(cover.toString(), "livro.pdf");

        assertEquals("livro.png", storage.fileName);
        assertEquals(3, storage.content.length);
        assertEquals("stored", result.urlPath());
    }

    private static class TrackingStorage implements FileStorage {
        private String fileName;
        private byte[] content;

        @Override
        public StoredFile store(InputStream inputStream, String fileName) {
            this.fileName = fileName;
            try { content = inputStream.readAllBytes(); } catch (Exception exception) { throw new AssertionError(exception); }
            return new StoredFile("stored");
        }

        @Override public InputStream retrieve(String path) { throw new UnsupportedOperationException(); }
        @Override public void delete(String path) { throw new UnsupportedOperationException(); }
    }
}
