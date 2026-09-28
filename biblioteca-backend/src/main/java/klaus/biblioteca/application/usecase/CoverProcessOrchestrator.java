package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.model.StoredFile;
import klaus.biblioteca.application.port.TemporaryFileStorage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public class CoverProcessOrchestrator {
    private final TemporaryFileStorage temporaryFileStorage;
    private final GenerateBookCover generateBookCover;
    private final StoreCover storeCover;

    public CoverProcessOrchestrator(TemporaryFileStorage temporaryFileStorage,
                                    GenerateBookCover generateBookCover,
                                    StoreCover storeCover) {
        this.temporaryFileStorage = Objects.requireNonNull(temporaryFileStorage);
        this.generateBookCover = Objects.requireNonNull(generateBookCover);
        this.storeCover = Objects.requireNonNull(storeCover);
    }

    public StoredFile execute(String temporaryPdfPath, String originalPdfFileName) {
        return execute(temporaryPdfPath, originalPdfFileName, null);
    }

    public StoredFile execute(String temporaryPdfPath, String originalPdfFileName, UUID bookId) {
        Objects.requireNonNull(temporaryPdfPath);

        if (!Files.isRegularFile(Path.of(temporaryPdfPath))) {
            throw new IllegalStateException("Temporary PDF does not exist: " + temporaryPdfPath);
        }
        Objects.requireNonNull(originalPdfFileName);

        String coverPath = generateBookCover.execute(temporaryPdfPath);
        try (TemporaryCover temporaryCover = new TemporaryCover(temporaryFileStorage, coverPath)) {
            return bookId == null ? storeCover.execute(temporaryCover.path(), originalPdfFileName)
                    : storeCover.execute(temporaryCover.path(), bookId);
        }
    }

    private static final class TemporaryCover implements AutoCloseable {
        private final TemporaryFileStorage storage;
        private final String path;

        private TemporaryCover(TemporaryFileStorage storage, String path) {
            this.storage = storage;
            this.path = Objects.requireNonNull(path);
        }

        private String path() {
            return path;
        }

        @Override
        public void close() {
            storage.delete(path);
        }
    }
}
