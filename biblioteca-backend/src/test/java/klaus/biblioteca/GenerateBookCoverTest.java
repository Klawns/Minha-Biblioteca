package klaus.biblioteca;

import klaus.biblioteca.application.usecase.GenerateBookCover;
import klaus.biblioteca.external.pdfbox.PdfBoxCoverRenderer;
import klaus.biblioteca.infra.cover.PdfBoxCoverGeneratorAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URISyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateBookCoverTest {

    @Test
    void generatesCoverInTemporaryDirectory(@TempDir Path outputDirectory) throws URISyntaxException, IOException {
        Path pdf = Path.of(getClass().getResource("/teste.pdf").toURI());
        GenerateBookCover useCase = new GenerateBookCover(
                new PdfBoxCoverGeneratorAdapter(new PdfBoxCoverRenderer(), outputDirectory));

        String coverPath = useCase.execute(pdf.toString());

        assertNotNull(coverPath);
        assertFalse(coverPath.isBlank());
        Path cover = Path.of(coverPath);
        assertTrue(Files.exists(cover));
        assertTrue(Files.size(cover) > 0);
        assertTrue(cover.startsWith(outputDirectory));
    }
}
