package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.port.BookRepository;
import klaus.biblioteca.application.port.FileStorage;
import klaus.biblioteca.domain.Book;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DeleteBook {
    private final BookRepository repository;
    private final FileStorage storage;
    private final Executor executor;
    public DeleteBook(BookRepository repository, FileStorage storage) { this(repository, storage, Runnable::run); }
    public DeleteBook(BookRepository repository, FileStorage storage, Executor executor) {
        this.repository = Objects.requireNonNull(repository); this.storage = Objects.requireNonNull(storage); this.executor = Objects.requireNonNull(executor);
    }
    public void execute(UUID id) {
        Book book = repository.findById(id).orElseThrow(() -> new klaus.biblioteca.application.exception.BookNotFoundException(id));
        CompletableFuture<Void> pdf = CompletableFuture.runAsync(() -> storage.delete(book.getPdfPath()), executor);
        CompletableFuture<Void> cover = CompletableFuture.runAsync(() -> storage.delete(book.getCoverPath()), executor);
        CompletableFuture.allOf(pdf, cover).join();
        repository.deleteById(id);
    }
}
