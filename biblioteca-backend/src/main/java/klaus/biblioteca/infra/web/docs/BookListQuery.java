package klaus.biblioteca.infra.web.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import klaus.biblioteca.domain.ReadingStatus;

@Schema(description = "Parâmetros de paginação e filtros da listagem de livros")
public class BookListQuery {
    @Schema(description = "Número da página, começando em zero", example = "0", defaultValue = "0", minimum = "0")
    private int page = 0;

    @Schema(description = "Quantidade de livros por página", example = "20", defaultValue = "20", minimum = "1")
    private int size = 20;

    @Schema(description = "Filtro por título", example = "Clean Code", nullable = true)
    private String title;

    @Schema(description = "Filtro por status de leitura", example = "PENDING", nullable = true)
    private ReadingStatus status;

    public int page() { return page; }
    public int size() { return size; }
    public String title() { return title; }
    public ReadingStatus status() { return status; }

    public int getPage() { return page; }
    public int getSize() { return size; }
    public String getTitle() { return title; }
    public ReadingStatus getStatus() { return status; }

    public void setPage(int page) { this.page = page; }
    public void setSize(int size) { this.size = size; }
    public void setTitle(String title) { this.title = title; }
    public void setStatus(ReadingStatus status) { this.status = status; }
}
