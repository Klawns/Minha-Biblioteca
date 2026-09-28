package klaus.biblioteca.application.usecase;
import klaus.biblioteca.application.model.*;
import klaus.biblioteca.application.port.BookRepository;
import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;
import java.util.Objects;
public class GetBooksByStatus {
    private final BookRepository repository;
    public GetBooksByStatus(BookRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public PageResult<Book> execute(ReadingStatus status, PageRequest request) { return repository.findByStatus(status, request); }
}
