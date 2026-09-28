package klaus.biblioteca.domain;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Book {
    private UUID id;
    private String title;
    private String pdfPath;
    private String coverPath;
    private ReadingStatus status;

    public Book(String title, String pdfPath, String coverPath, ReadingStatus status) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.pdfPath = pdfPath;
        this.coverPath = coverPath;
        this.status = status;
    }

    public Book(UUID id, String title, String pdfPath, String coverPath, ReadingStatus status) {
        this.id = Objects.requireNonNull(id);
        this.title = title;
        this.pdfPath = pdfPath;
        this.coverPath = coverPath;
        this.status = status;
    }

    public void markCompleted() {
        if (this.status == ReadingStatus.COMPLETED) {
            throw new DomainException("Book already marked completed");
        }
        this.status = ReadingStatus.COMPLETED;
    }

    public void markPending() {
        if (this.status == ReadingStatus.PENDING) {
            throw new DomainException("Book already marked pending");
        }
        this.status = ReadingStatus.PENDING;
    }

    public void changeTitle(String title) {
        this.title = Objects.requireNonNull(title);
    }

    public void changePdfPath(String pdfPath) {
        this.pdfPath = Objects.requireNonNull(pdfPath);
    }

    public void changeCoverPath(String coverPath) {
        this.coverPath = Objects.requireNonNull(coverPath);
    }

}
