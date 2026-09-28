package klaus.biblioteca.application.model;

public record PageRequest(int page, int size) {
    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("page must be non-negative");
        if (size < 1) throw new IllegalArgumentException("size must be positive");
    }

    public static PageRequest firstPage() { return new PageRequest(0, 20); }
}
