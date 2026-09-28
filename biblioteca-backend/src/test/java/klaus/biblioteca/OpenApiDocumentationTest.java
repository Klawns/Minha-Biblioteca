package klaus.biblioteca;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest {
    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void exposesOpenApiDocumentWithBookOperationsAndSchemas() throws Exception {
        String body = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode document = objectMapper.readTree(body);

        JsonNode books = document.path("paths").path("/books");
        JsonNode upload = document.path("paths").path("/books/upload");
        JsonNode book = document.path("paths").path("/books/{id}");
        assertTrue(books.has("get"));
        assertTrue(books.has("post"));
        assertTrue(upload.has("post"));
        assertTrue(book.has("get"));
        assertTrue(book.has("patch"));
        assertTrue(book.has("delete"));
        assertOperation(books.path("get"), "listBooks", "Listar livros");
        assertOperation(books.path("post"), "createBook", "Criar livro");
        assertOperation(upload.path("post"), "uploadBook", "Fazer upload de livro");
        assertOperation(book.path("get"), "getBook", "Buscar livro por identificador");
        assertOperation(book.path("patch"), "updateBook", "Atualizar livro");
        assertOperation(book.path("delete"), "deleteBook", "Excluir livro");

        assertTrue(book.path("get").path("tags").toString().contains("Books"));
        assertResponses(book.path("get"), "200", "400", "404", "500");
        assertResponses(book.path("patch"), "200", "400", "404", "500");
        assertResponses(book.path("delete"), "204", "400", "404", "500");
        assertResponses(books.path("post"), "201", "400", "500");
        assertResponses(upload.path("post"), "201", "400", "413", "500");
        assertFalse(books.path("post").path("responses").has("404"));
        assertFalse(upload.path("post").path("responses").has("404"));

        JsonNode schemas = document.path("components").path("schemas");
        assertNotNull(schemas.get("BookResponse"));
        assertNotNull(schemas.get("BookUpdateRequest"));
        assertNotNull(schemas.get("BookCreateRequest"));
        assertNotNull(schemas.get("BookListQuery"));
        assertNotNull(schemas.get("ApiErrorResponse"));
        assertTrue(upload.path("post").path("responses").path("413").path("content")
                .toString().contains("ApiErrorResponse"));
    }

    @Test
    void exposesSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    private static void assertOperation(JsonNode operation, String operationId, String summary) {
        org.junit.jupiter.api.Assertions.assertEquals(operationId, operation.path("operationId").asText());
        org.junit.jupiter.api.Assertions.assertEquals(summary, operation.path("summary").asText());
    }

    private static void assertResponses(JsonNode operation, String... codes) {
        for (String code : codes) assertTrue(operation.path("responses").has(code), "Missing response " + code);
    }
}
