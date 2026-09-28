package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.model.StoredFile;
import klaus.biblioteca.application.port.FileStorage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public class StoreCover {
    private final FileStorage fileStorage;

    public StoreCover(FileStorage fileStorage) {
        this.fileStorage = Objects.requireNonNull(fileStorage);
    }

    public StoredFile execute(String coverPath, String originalPdfFileName) {
        Objects.requireNonNull(coverPath);
        Objects.requireNonNull(originalPdfFileName);

        try (InputStream cover = Files.newInputStream(Path.of(coverPath))) {
            return fileStorage.store(cover, pngFileName(originalPdfFileName));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read generated cover", exception);
        }
    }

    public StoredFile execute(String coverPath, UUID bookId) {
        Objects.requireNonNull(bookId);
        try (InputStream cover = Files.newInputStream(Path.of(coverPath))) {
            return fileStorage.store(cover, "/books/" + bookId + "/cover");
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read generated cover", exception);
        }
    }

    private String pngFileName(String originalPdfFileName) {
        int extensionIndex = originalPdfFileName.lastIndexOf('.');
        return (extensionIndex > 0
                ? originalPdfFileName.substring(0, extensionIndex)
                : originalPdfFileName) + ".png";
    }
}
