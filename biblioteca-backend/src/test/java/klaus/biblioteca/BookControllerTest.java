package klaus.biblioteca;

import com.fasterxml.jackson.databind.ObjectMapper;
import klaus.biblioteca.application.usecase.BookUploadOrchestrator;
import klaus.biblioteca.application.usecase.CreateBook;
import klaus.biblioteca.application.usecase.DeleteBook;
import klaus.biblioteca.application.usecase.GetBookByID;
import klaus.biblioteca.application.usecase.ListBooks;
import klaus.biblioteca.application.usecase.UpdateBook;
import klaus.biblioteca.domain.Book;
import klaus.biblioteca.domain.ReadingStatus;
import klaus.biblioteca.infra.web.ApiExceptionHandler;
import klaus.biblioteca.infra.web.BookController;
import klaus.biblioteca.infra.web.dto.BookCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {
    @Mock ListBooks listBooks;
    @Mock GetBookByID getBook;
    @Mock UpdateBook updateBook;
    @Mock DeleteBook deleteBook;
    @Mock CreateBook createBook;
    @Mock BookUploadOrchestrator bookUploadOrchestrator;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        BookController controller = new BookController(listBooks, getBook, updateBook, deleteBook,
                createBook, bookUploadOrchestrator);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(new ObjectMapper()))
                .build();
    }

    @Test
    void createsBook() throws Exception {
        Book book = book();
        when(createBook.execute("Clean Code", "/books.pdf", "/cover.png")).thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                new BookCreateRequest("Clean Code", "/books.pdf", "/cover.png"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(book.getId().toString()))
                .andExpect(jsonPath("$.data.title").value("Clean Code"));
    }

    @Test
    void uploadsPdfAndDelegatesToOrchestrator() throws Exception {
        Book book = book();
        when(bookUploadOrchestrator.execute(any(InputStream.class), eq("book.pdf"))).thenReturn(book);
        MockMultipartFile file = new MockMultipartFile("file", "book.pdf", "application/pdf", "pdf".getBytes());

        mockMvc.perform(multipart("/books/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Clean Code"));

        verify(bookUploadOrchestrator).execute(any(InputStream.class), eq("book.pdf"));
    }

    @Test
    void rejectsEmptyUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "book.pdf", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/books/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"));
    }

    private Book book() {
        return new Book(UUID.randomUUID(), "Clean Code", "/books.pdf", "/cover.png", ReadingStatus.PENDING);
    }
}
