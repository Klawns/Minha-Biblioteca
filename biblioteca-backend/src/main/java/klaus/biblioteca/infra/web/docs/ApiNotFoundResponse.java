package klaus.biblioteca.infra.web.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import klaus.biblioteca.infra.web.dto.ApiErrorResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(@ApiResponse(responseCode = "404", description = "Livro não encontrado",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))))
public @interface ApiNotFoundResponse {}
