package klaus.biblioteca;

import klaus.biblioteca.application.port.BookRepository;
import klaus.biblioteca.application.port.FileStorage;
import klaus.biblioteca.application.usecase.DeleteBook;
import klaus.biblioteca.application.usecase.UpdateBook;
import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookUseCasesTest {
    @Test
    void updatesOnlyProvidedFieldsAndPreservesPaths() {
        BookRepository repository = mock(BookRepository.class);
        UUID id = UUID.randomUUID();
        Book book = new Book(id, "Old", "pdf", "cover", ReadingStatus.PENDING);
        when(repository.findById(id)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);

        Book result = new UpdateBook(repository).execute(id, "New", null);

        assertEquals("New", result.getTitle());
        assertEquals("pdf", result.getPdfPath());
        assertEquals("cover", result.getCoverPath());
        assertEquals(ReadingStatus.PENDING, result.getStatus());
        verify(repository).save(book);
    }

    @Test
    void doesNotDeleteDatabaseRecordWhenAFileDeletionFails() {
        BookRepository repository = mock(BookRepository.class);
        FileStorage storage = mock(FileStorage.class);
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(new Book(id, "Book", "pdf", "cover", ReadingStatus.PENDING)));
        doThrow(new IllegalStateException("storage")).when(storage).delete("pdf");

        assertThrows(RuntimeException.class, () -> new DeleteBook(repository, storage, (Executor) Runnable::run).execute(id));
        verify(repository, never()).deleteById(id);
    }
}
