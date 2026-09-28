package klaus.biblioteca.external.pdfbox;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;

public class PdfBoxTitleReader {

    public String read(String pdfPath) {
        File file = new File(pdfPath);

        try (PDDocument doc = Loader.loadPDF(file)) {
            return doc.getDocumentInformation().getTitle();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read PDF title", e);
        }
    }
}
