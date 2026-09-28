package klaus.biblioteca.infra.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Erro retornado pela API")
public record ApiErrorResponse(
        @Schema(description = "Mensagem do erro", example = "Book not found") String error) {}
