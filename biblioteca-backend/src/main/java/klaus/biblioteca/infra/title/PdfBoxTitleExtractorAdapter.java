package klaus.biblioteca.infra.title;

import klaus.biblioteca.application.port.TitleExtractor;
import klaus.biblioteca.external.pdfbox.PdfBoxTitleReader;
import java.util.Objects;

public class PdfBoxTitleExtractorAdapter implements TitleExtractor {
    private final PdfBoxTitleReader reader;

    public PdfBoxTitleExtractorAdapter(PdfBoxTitleReader reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    @Override
    public String extract(String pdfPath) {
        return reader.read(pdfPath);
    }
}
