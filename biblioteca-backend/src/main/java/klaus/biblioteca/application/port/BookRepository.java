package klaus.biblioteca.application.port;

import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;
import klaus.biblioteca.application.model.PageRequest;
import klaus.biblioteca.application.model.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(UUID id);
    PageResult<Book> findAll(PageRequest pageRequest);
    PageResult<Book> findByTitle(String title, PageRequest pageRequest);
    PageResult<Book> findByStatus(ReadingStatus status, PageRequest pageRequest);
    PageResult<Book> findByTitleAndStatus(String title, ReadingStatus status, PageRequest pageRequest);
    void deleteById(UUID id);
}
