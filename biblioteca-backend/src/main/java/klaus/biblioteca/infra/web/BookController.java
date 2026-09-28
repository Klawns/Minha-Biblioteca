package klaus.biblioteca.infra.web;

import klaus.biblioteca.application.model.PageResult;
import klaus.biblioteca.application.model.PageRequest;
import klaus.biblioteca.application.usecase.DeleteBook;
import klaus.biblioteca.application.usecase.GetBookByID;
import klaus.biblioteca.application.usecase.ListBooks;
import klaus.biblioteca.application.usecase.UpdateBook;
import klaus.biblioteca.application.usecase.BookUploadOrchestrator;
import klaus.biblioteca.application.usecase.CreateBook;
import klaus.biblioteca.application.port.FileStorage;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import klaus.biblioteca.domain.Book;
import klaus.biblioteca.infra.web.docs.BookApiDocumentation;
import klaus.biblioteca.infra.web.docs.BookListQuery;
import klaus.biblioteca.infra.web.dto.ApiResponse;
import klaus.biblioteca.infra.web.dto.BookResponse;
import klaus.biblioteca.infra.web.dto.BookUpdateRequest;
import klaus.biblioteca.infra.web.dto.BookCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
public class BookController implements BookApiDocumentation {
    private final ListBooks listBooks;
    private final GetBookByID getBook;
    private final UpdateBook updateBook;
    private final DeleteBook deleteBook;
    private final CreateBook createBook;
    private final BookUploadOrchestrator bookUploadOrchestrator;
    private final FileStorage fileStorage;

    @Autowired
    public BookController(ListBooks listBooks, GetBookByID getBook, UpdateBook updateBook, DeleteBook deleteBook,
                          CreateBook createBook, BookUploadOrchestrator bookUploadOrchestrator, FileStorage fileStorage) {
        this.listBooks=listBooks; this.getBook=getBook; this.updateBook=updateBook; this.deleteBook=deleteBook;
        this.createBook = createBook; this.bookUploadOrchestrator = bookUploadOrchestrator;
        this.fileStorage = fileStorage;
    }

    public BookController(ListBooks listBooks, GetBookByID getBook, UpdateBook updateBook, DeleteBook deleteBook,
                          CreateBook createBook, BookUploadOrchestrator bookUploadOrchestrator) {
        this(listBooks, getBook, updateBook, deleteBook, createBook, bookUploadOrchestrator,
                new klaus.biblioteca.infra.storage.local.LocalFileStorage());
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse<BookResponse>> create(@RequestBody BookCreateRequest request) {
        Book book = createBook.execute(request.title(), request.pdfPath(), request.coverPath());
        return ResponseEntity.status(201).body(new ApiResponse<>(BookResponse.from(book), null));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Override
    public ResponseEntity<ApiResponse<BookResponse>> upload(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null
                || !file.getOriginalFilename().toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")
                || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("PDF file must be provided");
        }
        try {
            Book book = bookUploadOrchestrator.execute(file.getInputStream(), file.getOriginalFilename());
            return ResponseEntity.status(201).body(new ApiResponse<>(BookResponse.from(book), null));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read uploaded PDF", exception);
        }
    }

    @GetMapping
    @Override
    public ApiResponse<List<BookResponse>> list(@ModelAttribute BookListQuery query) {
        PageResult<Book> result = listBooks.execute(new PageRequest(query.page(), query.size()), query.title(), query.status());
        return new ApiResponse<>(result.content().stream().map(BookResponse::from).toList(),
                new ApiResponse.ApiMetadata(result.page(), result.size(), result.totalElements(), result.totalPages()));
    }
    @GetMapping("/{id}")
    @Override
    public ApiResponse<BookResponse> get(@PathVariable UUID id) {
        return new ApiResponse<>(BookResponse.from(getBook.execute(id)), null);
    }

    @GetMapping("/{id}/cover")
    @Override
    public ResponseEntity<InputStreamResource> cover(@PathVariable UUID id) {
        return fileResponse(getBook.execute(id).getCoverPath(), MediaType.IMAGE_PNG, "inline");
    }

    @GetMapping("/{id}/pdf")
    @Override
    public ResponseEntity<InputStreamResource> pdf(@PathVariable UUID id) {
        return fileResponse(getBook.execute(id).getPdfPath(), MediaType.APPLICATION_PDF, "attachment");
    }

    private ResponseEntity<InputStreamResource> fileResponse(String path, MediaType type, String disposition) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        headers.set("Content-Disposition", disposition);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(fileStorage.retrieve(path)));
    }
    @PatchMapping("/{id}")
    @Override
    public ApiResponse<BookResponse> update(@PathVariable UUID id, @RequestBody BookUpdateRequest request) {
        return new ApiResponse<>(BookResponse.from(updateBook.execute(id, request.title(), request.status())), null);
    }
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteBook.execute(id); return ResponseEntity.noContent().build();
    }
}
