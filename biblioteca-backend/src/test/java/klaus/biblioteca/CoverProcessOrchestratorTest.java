package klaus.biblioteca;

import klaus.biblioteca.application.model.StoredFile;
import klaus.biblioteca.application.port.FileStorage;
import klaus.biblioteca.application.port.TemporaryFileStorage;
import klaus.biblioteca.application.usecase.CoverProcessOrchestrator;
import klaus.biblioteca.application.usecase.GenerateBookCover;
import klaus.biblioteca.application.usecase.StoreCover;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoverProcessOrchestratorTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void generatesStoresAndRemovesOnlyTheTemporaryCover() throws Exception {
        Path pdf = Files.createFile(temporaryDirectory.resolve("livro.pdf"));
        Path cover = temporaryDirectory.resolve("livro.png");
        RecordingTemporaryStorage temporary = new RecordingTemporaryStorage();
        RecordingStoreCover store = new RecordingStoreCover();
        var orchestrator = new CoverProcessOrchestrator(temporary,
                new GenerateBookCover(path -> {
                    assertEquals(pdf.toString(), path);
                    try {
                        Files.writeString(cover, "cover");
                    } catch (Exception exception) {
                        throw new IllegalStateException(exception);
                    }
                    return cover.toString();
                }), store);

        StoredFile result = orchestrator.execute(pdf.toString(), "livro.pdf");

        assertEquals("livro.png", result.urlPath());
        assertEquals(cover.toString(), store.coverPath);
        assertEquals("livro.pdf", store.originalName);
        assertEquals(List.of(cover.toString()), temporary.deleted);
        assertTrue(Files.exists(pdf));
        assertFalse(Files.exists(cover));
    }

    @Test
    void rejectsMissingPdfBeforeCallingGenerator() {
        RecordingTemporaryStorage temporary = new RecordingTemporaryStorage();
        var generated = new boolean[1];
        var orchestrator = new CoverProcessOrchestrator(temporary,
                new GenerateBookCover(path -> {
                    generated[0] = true;
                    return "cover-path";
                }), new RecordingStoreCover());

        assertThrows(IllegalStateException.class,
                () -> orchestrator.execute(temporaryDirectory.resolve("missing.pdf").toString(), "livro.pdf"));
        assertFalse(generated[0]);
        assertTrue(temporary.deleted.isEmpty());
    }

    @Test
    void generationFailureDoesNotDeleteA_nonexistentCover() throws Exception {
        Path pdf = Files.createFile(temporaryDirectory.resolve("livro.pdf"));
        IllegalStateException primary = new IllegalStateException("generation");
        RecordingTemporaryStorage temporary = new RecordingTemporaryStorage();
        var orchestrator = new CoverProcessOrchestrator(temporary,
                new GenerateBookCover(path -> { throw primary; }), new RecordingStoreCover());

        assertSame(primary, assertThrows(IllegalStateException.class,
                () -> orchestrator.execute(pdf.toString(), "livro.pdf")));
        assertTrue(temporary.deleted.isEmpty());
        assertTrue(Files.exists(pdf));
    }

    @Test
    void storageFailureStillRemovesTheTemporaryCover() throws Exception {
        Path pdf = Files.createFile(temporaryDirectory.resolve("livro.pdf"));
        Path cover = Files.createFile(temporaryDirectory.resolve("livro.png"));
        IllegalStateException primary = new IllegalStateException("storage");
        RecordingTemporaryStorage temporary = new RecordingTemporaryStorage();
        var orchestrator = new CoverProcessOrchestrator(temporary,
                new GenerateBookCover(path -> cover.toString()),
                new RecordingStoreCover(primary));

        assertSame(primary, assertThrows(IllegalStateException.class,
                () -> orchestrator.execute(pdf.toString(), "livro.pdf")));
        assertEquals(List.of(cover.toString()), temporary.deleted);
        assertFalse(Files.exists(cover));
        assertTrue(Files.exists(pdf));
    }

    @Test
    void cleanupFailureIsSuppressedOnThePrimaryFailure() throws Exception {
        Path pdf = Files.createFile(temporaryDirectory.resolve("livro.pdf"));
        Path cover = Files.createFile(temporaryDirectory.resolve("livro.png"));
        IllegalStateException primary = new IllegalStateException("storage");
        IllegalStateException cleanup = new IllegalStateException("cleanup");
        RecordingTemporaryStorage temporary = new RecordingTemporaryStorage(cleanup);
        var orchestrator = new CoverProcessOrchestrator(temporary,
                new GenerateBookCover(path -> cover.toString()),
                new RecordingStoreCover(primary));

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> orchestrator.execute(pdf.toString(), "livro.pdf"));

        assertSame(primary, thrown);
        assertEquals(List.of(cleanup), List.of(thrown.getSuppressed()));
        assertTrue(Files.exists(pdf));
    }

    private static class RecordingTemporaryStorage implements TemporaryFileStorage {
        private final List<String> deleted = new ArrayList<>();
        private final RuntimeException deletionFailure;

        private RecordingTemporaryStorage() {
            this(null);
        }

        private RecordingTemporaryStorage(RuntimeException deletionFailure) {
            this.deletionFailure = deletionFailure;
        }

        @Override
        public String create(InputStream input, String prefix, String suffix) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(String path) {
            deleted.add(path);
            try {
                Files.deleteIfExists(Path.of(path));
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
            if (deletionFailure != null) {
                throw deletionFailure;
            }
        }
    }

    private static class RecordingStoreCover extends StoreCover {
        private final RuntimeException failure;
        private String coverPath;
        private String originalName;

        private RecordingStoreCover() {
            this(null);
        }

        private RecordingStoreCover(RuntimeException failure) {
            super(new NoOpFileStorage());
            this.failure = failure;
        }

        @Override
        public StoredFile execute(String coverPath, String originalName) {
            this.coverPath = coverPath;
            this.originalName = originalName;
            if (failure != null) {
                throw failure;
            }
            return new StoredFile("livro.png");
        }
    }

    private static class NoOpFileStorage implements FileStorage {
        @Override public StoredFile store(InputStream input, String name) { return new StoredFile(name); }
        @Override public InputStream retrieve(String path) { throw new UnsupportedOperationException(); }
        @Override public void delete(String path) { throw new UnsupportedOperationException(); }
    }
}
