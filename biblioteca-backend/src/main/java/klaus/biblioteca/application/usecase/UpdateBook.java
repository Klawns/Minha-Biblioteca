package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.exception.BookNotFoundException;
import klaus.biblioteca.application.port.BookRepository;
import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;
import java.util.Objects;
import java.util.UUID;

public class UpdateBook {
    private final BookRepository repository;
    public UpdateBook(BookRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public Book execute(UUID id, String title, ReadingStatus status) {
        if (title == null && status == null) throw new IllegalArgumentException("At least one field must be provided");
        Book book = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        if (title != null) {
            if (title.isBlank()) throw new IllegalArgumentException("title must not be blank");
            book.changeTitle(title);
        }
        if (status != null) {
            if (status == ReadingStatus.COMPLETED) book.markCompleted(); else book.markPending();
        }
        return repository.save(book);
    }
}
