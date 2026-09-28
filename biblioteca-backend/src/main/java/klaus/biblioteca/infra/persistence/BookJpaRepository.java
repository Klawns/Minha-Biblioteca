package klaus.biblioteca.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import klaus.biblioteca.domain.ReadingStatus;

import java.util.UUID;

public interface BookJpaRepository extends JpaRepository<BookEntity, UUID> {
    Page<BookEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<BookEntity> findByStatus(ReadingStatus status, Pageable pageable);
    Page<BookEntity> findByTitleContainingIgnoreCaseAndStatus(String title, ReadingStatus status, Pageable pageable);
}
