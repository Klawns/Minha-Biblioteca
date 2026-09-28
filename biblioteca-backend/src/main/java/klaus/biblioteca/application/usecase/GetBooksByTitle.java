package klaus.biblioteca.application.usecase;
import klaus.biblioteca.application.model.*;
import klaus.biblioteca.application.port.BookRepository;
import klaus.biblioteca.domain.Book;
import java.util.Objects;
public class GetBooksByTitle {
    private final BookRepository repository;
    public GetBooksByTitle(BookRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public PageResult<Book> execute(String title, PageRequest request) { return repository.findByTitle(title, request); }
}
