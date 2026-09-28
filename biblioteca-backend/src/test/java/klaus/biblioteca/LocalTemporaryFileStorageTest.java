package klaus.biblioteca;

import klaus.biblioteca.infra.storage.local.LocalTemporaryFileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class LocalTemporaryFileStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsAndDeletesTemporaryPdf() throws Exception {
        LocalTemporaryFileStorage storage = new LocalTemporaryFileStorage(temporaryDirectory);
        byte[] content = "%PDF-1.7 test".getBytes(StandardCharsets.UTF_8);

        String path = storage.create(new ByteArrayInputStream(content), "pdf-", ".pdf");
        Path createdFile = Path.of(path);

        assertTrue(createdFile.isAbsolute());
        assertEquals(createdFile.getParent(), temporaryDirectory);
        assertTrue(Files.isRegularFile(createdFile));
        assertArrayEquals(content, Files.readAllBytes(createdFile));

        storage.delete(path);
        assertFalse(Files.exists(createdFile));
        storage.delete(path);
    }
}
