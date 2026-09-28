package klaus.biblioteca.infra.persistence;

import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "books")
public class BookEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "pdf_path", nullable = false)
    private String pdfPath;

    @Column(name = "cover_path", nullable = false)
    private String coverPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    protected BookEntity() {
    }

    public BookEntity(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.pdfPath = book.getPdfPath();
        this.coverPath = book.getCoverPath();
        this.status = book.getStatus();
    }

    public Book toDomain() {
        return new Book(id, title, pdfPath, coverPath, status);
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getPdfPath() { return pdfPath; }
    public String getCoverPath() { return coverPath; }
    public ReadingStatus getStatus() { return status; }
}
