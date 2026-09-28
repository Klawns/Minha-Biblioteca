package klaus.biblioteca.application.usecase;
import klaus.biblioteca.application.model.*;
import klaus.biblioteca.application.port.BookRepository;
import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;
import java.util.Objects;
public class ListBooks {
    private final BookRepository repository;
    public ListBooks(BookRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public PageResult<Book> execute(PageRequest request) { return repository.findAll(request); }
    public PageResult<Book> execute(PageRequest request, String title, ReadingStatus status) {
        if (title != null && status != null) return repository.findByTitleAndStatus(title, status, request);
        if (title != null) return repository.findByTitle(title, request);
        if (status != null) return repository.findByStatus(status, request);
        return execute(request);
    }
}
