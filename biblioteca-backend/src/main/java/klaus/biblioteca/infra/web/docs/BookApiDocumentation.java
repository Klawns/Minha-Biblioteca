package klaus.biblioteca.infra.web.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import klaus.biblioteca.infra.web.dto.BookResponse;
import klaus.biblioteca.infra.web.dto.BookUpdateRequest;
import klaus.biblioteca.infra.web.dto.BookCreateRequest;
import klaus.biblioteca.infra.web.dto.BookUploadRequest;
import io.swagger.v3.oas.annotations.media.Encoding;
import org.springframework.web.multipart.MultipartFile;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Books", description = "Operações de livros")
public interface BookApiDocumentation {
    @Operation(operationId = "createBook", summary = "Criar livro")
    @ApiStandardErrors
    @ApiResponse(responseCode = "201", description = "Livro criado")
    ResponseEntity<klaus.biblioteca.infra.web.dto.ApiResponse<BookResponse>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    description = "Dados do livro",
                    content = @Content(schema = @Schema(implementation = BookCreateRequest.class)))
            BookCreateRequest request);

    @Operation(operationId = "uploadBook", summary = "Fazer upload de livro")
    @ApiStandardErrors
    @ApiPayloadTooLargeResponse
    @ApiResponse(responseCode = "201", description = "Livro enviado e processado")
    ResponseEntity<klaus.biblioteca.infra.web.dto.ApiResponse<BookResponse>> upload(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    description = "Arquivo PDF do livro",
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(implementation = BookUploadRequest.class),
                            encoding = @Encoding(name = "file", contentType = "application/pdf")))
            MultipartFile file);

    @Operation(operationId = "listBooks", summary = "Listar livros")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Livros listados")
    klaus.biblioteca.infra.web.dto.ApiResponse<List<BookResponse>> list(@ParameterObject BookListQuery query);

    @Operation(operationId = "getBook", summary = "Buscar livro por identificador")
    @Parameters(@Parameter(name = "id", in = ParameterIn.PATH, required = true,
            description = "Identificador do livro", schema = @Schema(format = "uuid")))
    @ApiStandardErrors
    @ApiNotFoundResponse
    @ApiResponse(responseCode = "200", description = "Livro encontrado")
    klaus.biblioteca.infra.web.dto.ApiResponse<BookResponse> get(UUID id);

    @Operation(operationId = "getBookCover", summary = "Visualizar capa do livro")
    @Parameters(@Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Identificador do livro", schema = @Schema(format = "uuid")))
    @ApiStandardErrors @ApiNotFoundResponse
    @ApiResponse(responseCode = "200", description = "Capa encontrada", content = @Content(mediaType = "image/png"))
    ResponseEntity<InputStreamResource> cover(UUID id);

    @Operation(operationId = "downloadBookPdf", summary = "Baixar PDF do livro")
    @Parameters(@Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Identificador do livro", schema = @Schema(format = "uuid")))
    @ApiStandardErrors @ApiNotFoundResponse
    @ApiResponse(responseCode = "200", description = "PDF encontrado", content = @Content(mediaType = "application/pdf"))
    ResponseEntity<InputStreamResource> pdf(UUID id);

    @Operation(operationId = "updateBook", summary = "Atualizar livro")
    @Parameters(@Parameter(name = "id", in = ParameterIn.PATH, required = true,
            description = "Identificador do livro", schema = @Schema(format = "uuid")))
    @ApiStandardErrors
    @ApiNotFoundResponse
    @ApiResponse(responseCode = "200", description = "Livro atualizado")
    klaus.biblioteca.infra.web.dto.ApiResponse<BookResponse> update(UUID id,
                                                                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true, description = "Dados para atualização",
                    content = @Content(schema = @Schema(implementation = BookUpdateRequest.class)))
            BookUpdateRequest request);

    @Operation(operationId = "deleteBook", summary = "Excluir livro")
    @Parameters(@Parameter(name = "id", in = ParameterIn.PATH, required = true,
            description = "Identificador do livro", schema = @Schema(format = "uuid")))
    @ApiStandardErrors
    @ApiNotFoundResponse
    @ApiResponse(responseCode = "204", description = "Livro excluído")
    ResponseEntity<Void> delete(UUID id);
}
