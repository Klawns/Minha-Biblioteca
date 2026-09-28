package klaus.biblioteca.infra.web.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta padrão da API")
public record ApiResponse<T>(T data, ApiMetadata metadata) {
    @Schema(description = "Metadados da paginação")
    public record ApiMetadata(
            @Schema(example = "0") int page,
            @Schema(example = "20") int size,
            @Schema(example = "42") long totalElements,
            @Schema(example = "3") int totalPages) {}
}
