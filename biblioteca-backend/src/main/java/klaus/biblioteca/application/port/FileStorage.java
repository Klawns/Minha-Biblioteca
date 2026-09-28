package klaus.biblioteca.application.port;

import klaus.biblioteca.application.model.StoredFile;
import java.io.InputStream;

public interface FileStorage {
    StoredFile store(InputStream inputStream, String fileName);
    InputStream retrieve(String path);
    void delete(String path);
}
