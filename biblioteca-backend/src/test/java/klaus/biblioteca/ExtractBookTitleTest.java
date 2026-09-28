package klaus.biblioteca;

import klaus.biblioteca.application.port.TitleExtractor;
import klaus.biblioteca.application.usecase.ExtractBookTitle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtractBookTitleTest {

    @Test
    void keepsTitleReadFromPdfWhenItIsAvailable() {
        ExtractBookTitle useCase = new ExtractBookTitle(ignored -> "Título do PDF");

        assertEquals("Título do PDF", useCase.execute("/books/arquivo.pdf"));
    }

    @Test
    void usesFormattedFileNameWhenPdfTitleIsMissingOrBlank() {
        for (String pdfTitle : new String[]{null, "", "   \t"}) {
            TitleExtractor extractor = ignored -> pdfTitle;
            ExtractBookTitle useCase = new ExtractBookTitle(extractor);

            assertEquals("meu livro de testes",
                    useCase.execute("/books/meu-livro_de-testes.pdf"));
        }
    }
}
