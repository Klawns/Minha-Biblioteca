package klaus.biblioteca.infra.web.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;
import java.util.UUID;
@Schema(description = "Livro retornado pela API")
public record BookResponse(
        @Schema(description = "Identificador do livro", format = "uuid") UUID id,
        @Schema(description = "Título do livro", example = "Clean Code") String title,
        @Schema(description = "URL da API para baixar o PDF") String pdfUrl,
        @Schema(description = "URL da API para visualizar a capa") String coverUrl,
        @Schema(description = "Status de leitura") ReadingStatus status) {
    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), "/books/" + book.getId() + "/pdf",
                "/books/" + book.getId() + "/cover", book.getStatus());
    }
}
