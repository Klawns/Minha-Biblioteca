package klaus.biblioteca.infra.storage.local;

public class MediaFileNotFoundException extends RuntimeException {
    public MediaFileNotFoundException(String path, Throwable cause) {
        super("Media file not found: " + path, cause);
    }
}
