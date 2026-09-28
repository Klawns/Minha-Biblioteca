package klaus.biblioteca.infra.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação direta de um livro")
public record BookCreateRequest(
        @Schema(description = "Título do livro", example = "Clean Code") String title,
        @Schema(description = "Caminho do arquivo PDF", example = "/storage/clean-code.pdf") String pdfPath,
        @Schema(description = "Caminho da capa", example = "/storage/clean-code.png") String coverPath) {}
