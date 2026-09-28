package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.port.TitleExtractor;
import java.util.Objects;

public class ExtractBookTitle {
    private final TitleExtractor titleExtractor;

    public ExtractBookTitle(TitleExtractor titleExtractor) {
        this.titleExtractor = Objects.requireNonNull(titleExtractor);
    }

    public String execute(String pdfPath) {
        String title = titleExtractor.extract(pdfPath);
        return title == null || title.isBlank() ? fileNameAsTitle(pdfPath) : title;
    }

    private String fileNameAsTitle(String pdfPath) {
        String fileName = pdfPath.substring(Math.max(pdfPath.lastIndexOf('/'), pdfPath.lastIndexOf('\\')) + 1);
        String withoutExtension = fileName.replaceFirst("(?i)\\.pdf$", "");
        return withoutExtension.replace('-', ' ').replace('_', ' ').trim();
    }
}
