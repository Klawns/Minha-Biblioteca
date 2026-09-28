package klaus.biblioteca.infra.persistence;

import klaus.biblioteca.application.port.BookRepository;
import klaus.biblioteca.application.model.PageRequest;
import klaus.biblioteca.application.model.PageResult;
import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookRepositoryAdapter implements BookRepository {
    private final BookJpaRepository repository;

    public BookRepositoryAdapter(BookJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Book save(Book book) {
        return repository.save(new BookEntity(book)).toDomain();
    }

    @Override public Optional<Book> findById(UUID id) {
        return repository.findById(id).map(BookEntity::toDomain);
    }

    @Override public PageResult<Book> findAll(PageRequest request) {
        return map(repository.findAll(toSpring(request)));
    }

    @Override public PageResult<Book> findByTitle(String title, PageRequest request) {
        return map(repository.findByTitleContainingIgnoreCase(title, toSpring(request)));
    }

    @Override public PageResult<Book> findByStatus(ReadingStatus status, PageRequest request) {
        return map(repository.findByStatus(status, toSpring(request)));
    }

    @Override public PageResult<Book> findByTitleAndStatus(String title, ReadingStatus status, PageRequest request) {
        return map(repository.findByTitleContainingIgnoreCaseAndStatus(title, status, toSpring(request)));
    }

    @Override public void deleteById(UUID id) { repository.deleteById(id); }

    private org.springframework.data.domain.PageRequest toSpring(PageRequest request) {
        return org.springframework.data.domain.PageRequest.of(request.page(), request.size());
    }

    private PageResult<Book> map(Page<BookEntity> page) {
        return new PageResult<>(page.getContent().stream().map(BookEntity::toDomain).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
