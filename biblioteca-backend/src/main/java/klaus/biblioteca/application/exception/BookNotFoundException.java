package klaus.biblioteca.application.exception;

import java.util.UUID;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(UUID id) { super("Book not found: " + id); }
}
