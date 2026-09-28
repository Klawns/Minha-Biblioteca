package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.port.CoverGenerator;
import java.util.Objects;

public class GenerateBookCover {
    private final CoverGenerator coverGenerator;

    public GenerateBookCover(CoverGenerator coverGenerator) {
        this.coverGenerator = Objects.requireNonNull(coverGenerator);
    }

    public String execute(String pdfPath) {
        return coverGenerator.generate(pdfPath);
    }
}
