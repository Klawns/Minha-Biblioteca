package klaus.biblioteca.infra.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Arquivo PDF do livro")
public class BookUploadRequest {
    @Schema(description = "Arquivo PDF", type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED)
    private Object file;

    public Object getFile() { return file; }
    public void setFile(Object file) { this.file = file; }
}
