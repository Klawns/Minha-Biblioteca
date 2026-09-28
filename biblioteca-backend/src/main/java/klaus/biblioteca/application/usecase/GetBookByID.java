package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.exception.BookNotFoundException;
import klaus.biblioteca.application.port.BookRepository;
import klaus.biblioteca.domain.Book;
import java.util.Objects;
import java.util.UUID;

public class GetBookByID {
    private final BookRepository repository;
    public GetBookByID(BookRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public Book execute(UUID id) { return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id)); }
}
