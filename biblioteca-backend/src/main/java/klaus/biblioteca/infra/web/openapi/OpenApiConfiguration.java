package klaus.biblioteca.infra.web.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import klaus.biblioteca.infra.web.docs.BookListQuery;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Biblioteca API",
        description = "API para gerenciamento de livros e status de leitura.",
        version = "1.0.0"))
public class OpenApiConfiguration {
    @Bean
    OpenApiCustomizer bookListQuerySchemaCustomizer() {
        return openAPI -> {
        ResolvedSchema resolved = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(BookListQuery.class));
        Components components = openAPI.getComponents();
        if (components == null) {
            components = new Components();
            openAPI.setComponents(components);
        }
        resolved.referencedSchemas.forEach(components::addSchemas);
        };
    }
}
