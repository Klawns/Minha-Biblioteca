package klaus.biblioteca.infra.web.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import klaus.biblioteca.domain.ReadingStatus;
@JsonIgnoreProperties(ignoreUnknown = false)
@Schema(description = "Dados permitidos para atualização de um livro")
public record BookUpdateRequest(
        @Schema(description = "Novo título", example = "Clean Code") String title,
        @Schema(description = "Novo status de leitura", example = "COMPLETED") ReadingStatus status) {}
