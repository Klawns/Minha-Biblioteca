package klaus.biblioteca.infra.cover;

import klaus.biblioteca.application.port.CoverGenerator;
import klaus.biblioteca.external.pdfbox.PdfBoxCoverRenderer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class PdfBoxCoverGeneratorAdapter implements CoverGenerator {
    private final PdfBoxCoverRenderer renderer;
    private final Path coverDirectory;

    public PdfBoxCoverGeneratorAdapter(PdfBoxCoverRenderer renderer, Path coverDirectory) {
        this.renderer = Objects.requireNonNull(renderer);
        this.coverDirectory = Objects.requireNonNull(coverDirectory);
    }

    @Override
    public String generate(String pdfPath) {
        Objects.requireNonNull(pdfPath);
        BufferedImage image = renderer.render(pdfPath);
        Path output = coverDirectory.resolve(outputFileName(pdfPath));

        try {
            Files.createDirectories(coverDirectory);
            if (!ImageIO.write(image, "png", output.toFile())) {
                throw new IOException("No PNG writer is available");
            }
            return output.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to store generated cover", e);
        }
    }

    private String outputFileName(String pdfPath) {
        String fileName = Path.of(pdfPath).getFileName().toString();
        int extensionIndex = fileName.lastIndexOf('.');
        String baseName = extensionIndex > 0 ? fileName.substring(0, extensionIndex) : fileName;
        return baseName + ".png";
    }
}
