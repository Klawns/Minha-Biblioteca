package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.model.StoredFile;
import klaus.biblioteca.application.port.FileStorage;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

public class StorePdf {
    private final FileStorage fileStorage;

    public StorePdf(FileStorage fileStorage) {
        this.fileStorage = Objects.requireNonNull(fileStorage);
    }

    public StoredFile execute(InputStream pdf, String fileName) {
        return fileStorage.store(pdf, fileName);
    }

    public StoredFile execute(InputStream pdf, UUID bookId) {
        return fileStorage.store(pdf, "/books/" + bookId + "/pdf");
    }
}
