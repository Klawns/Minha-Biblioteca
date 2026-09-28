package klaus.biblioteca.external.pdfbox;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;

public class PdfBoxCoverRenderer {
    public BufferedImage render(String pdfPath) {
        File file = new File(pdfPath);

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFRenderer renderer = new PDFRenderer(document);
            return renderer.renderImageWithDPI(0, 300, ImageType.RGB);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to render the first PDF page", e);
        }
    }

}
