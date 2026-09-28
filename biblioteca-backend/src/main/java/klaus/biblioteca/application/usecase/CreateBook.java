package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.port.BookRepository;
import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;

import java.util.Objects;
import java.util.UUID;

public class CreateBook {
    private final BookRepository repository;

    public CreateBook(BookRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Book execute(String title, String pdfPath, String coverPath) {
        return repository.save(new Book(title, pdfPath, coverPath, ReadingStatus.PENDING));
    }

    public Book execute(UUID id, String title, String pdfPath, String coverPath) {
        return repository.save(new Book(id, title, pdfPath, coverPath, ReadingStatus.PENDING));
    }
}
