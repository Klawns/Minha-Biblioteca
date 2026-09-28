package klaus.biblioteca.infra.web.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import klaus.biblioteca.infra.web.dto.ApiErrorResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(@ApiResponse(responseCode = "413", description = "Arquivo excede o limite de 60 MB",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))))
public @interface ApiPayloadTooLargeResponse {}
