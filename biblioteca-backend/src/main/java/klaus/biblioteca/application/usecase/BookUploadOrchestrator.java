package klaus.biblioteca.application.usecase;

import klaus.biblioteca.application.model.StoredFile;
import klaus.biblioteca.application.port.TemporaryFileStorage;
import klaus.biblioteca.domain.Book;

import java.io.InputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.UUID;

public class BookUploadOrchestrator {
    private final TemporaryFileStorage temporaryFileStorage;
    private final ExtractBookTitle extractBookTitle;
    private final CoverProcessOrchestrator coverProcessOrchestrator;
    private final StorePdf storePdf;
    private final CreateBook createBook;
    private final Executor executor;

    public BookUploadOrchestrator(TemporaryFileStorage temporaryFileStorage,
                                  ExtractBookTitle extractBookTitle,
                                  CoverProcessOrchestrator coverProcessOrchestrator,
                                  StorePdf storePdf,
                                  CreateBook createBook,
                                  Executor executor) {
        this.temporaryFileStorage = Objects.requireNonNull(temporaryFileStorage);
        this.extractBookTitle = Objects.requireNonNull(extractBookTitle);
        this.coverProcessOrchestrator = Objects.requireNonNull(coverProcessOrchestrator);
        this.storePdf = Objects.requireNonNull(storePdf);
        this.createBook = Objects.requireNonNull(createBook);
        this.executor = Objects.requireNonNull(executor);
    }

    public Book execute(InputStream pdf, String originalFileName) {
        Objects.requireNonNull(pdf);
        Objects.requireNonNull(originalFileName);

        String temporaryPdfPath = temporaryFileStorage.create(pdf, "book-", ".pdf");
        UUID bookId = UUID.randomUUID();
        Throwable primaryFailure = null;
        try {
            CompletableFuture<String> title = CompletableFuture.supplyAsync(
                    () -> extractBookTitle.execute(temporaryPdfPath), executor);
            CompletableFuture<StoredFile> cover = CompletableFuture.supplyAsync(
                    () -> coverProcessOrchestrator.execute(temporaryPdfPath, originalFileName, bookId), executor);

            join(CompletableFuture.allOf(title, cover));
            String extractedTitle = join(title);
            StoredFile storedCover = join(cover);
            StoredFile storedPdf;

            try (InputStream temporaryPdf = temporaryFileStorage.open(temporaryPdfPath)) {
                storedPdf = storePdf.execute(temporaryPdf, bookId);
            } catch (IOException exception) {
                throw new IllegalStateException("Could not read temporary PDF", exception);
            }

            return createBook.execute(bookId, extractedTitle, storedPdf.urlPath(), storedCover.urlPath());

        } catch (RuntimeException | Error failure) {
            primaryFailure = failure;
            throw failure;
        } finally {
            try {
                temporaryFileStorage.delete(temporaryPdfPath);
            } catch (RuntimeException | Error cleanupFailure) {
                if (primaryFailure != null) {
                    primaryFailure.addSuppressed(cleanupFailure);
                } else {
                    throw cleanupFailure;
                }
            }
        }
    }

    private <T> T join(CompletableFuture<T> future) {
        try {
            return future.join();
        } catch (CompletionException exception) {
            Throwable cause = exception.getCause();
            while (cause instanceof CompletionException && cause.getCause() != null) {
                cause = cause.getCause();
            }
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw exception;
        }
    }
}
