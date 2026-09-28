package klaus.biblioteca.application.model;

import java.util.List;
import java.util.Objects;

public record PageResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    public PageResult {
        content = List.copyOf(Objects.requireNonNull(content));
        if (page < 0 || size < 1 || totalElements < 0 || totalPages < 0)
            throw new IllegalArgumentException("Invalid page metadata");
    }
}
