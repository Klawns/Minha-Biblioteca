package klaus.biblioteca.infra;

import klaus.biblioteca.application.port.CoverGenerator;
import klaus.biblioteca.application.port.FileStorage;
import klaus.biblioteca.application.port.TitleExtractor;
import klaus.biblioteca.application.port.TemporaryFileStorage;
import klaus.biblioteca.application.usecase.BookUploadOrchestrator;
import klaus.biblioteca.application.usecase.CreateBook;
import klaus.biblioteca.application.usecase.CoverProcessOrchestrator;
import klaus.biblioteca.application.usecase.ExtractBookTitle;
import klaus.biblioteca.application.usecase.GenerateBookCover;
import klaus.biblioteca.application.usecase.StoreCover;
import klaus.biblioteca.application.usecase.StorePdf;
import klaus.biblioteca.application.usecase.DeleteBook;
import klaus.biblioteca.application.usecase.GetBookByID;
import klaus.biblioteca.application.usecase.GetBooksByStatus;
import klaus.biblioteca.application.usecase.GetBooksByTitle;
import klaus.biblioteca.application.usecase.ListBooks;
import klaus.biblioteca.application.usecase.UpdateBook;
import klaus.biblioteca.external.pdfbox.PdfBoxCoverRenderer;
import klaus.biblioteca.external.pdfbox.PdfBoxTitleReader;
import klaus.biblioteca.infra.cover.PdfBoxCoverGeneratorAdapter;
import klaus.biblioteca.infra.storage.local.LocalFileStorage;
import klaus.biblioteca.infra.storage.local.LocalTemporaryFileStorage;
import klaus.biblioteca.external.r2.R2FileStorage;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import java.net.URI;
import klaus.biblioteca.infra.title.PdfBoxTitleExtractorAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BibliotecaConfiguration {
    @Bean
    TemporaryFileStorage temporaryFileStorage(
            @Value("${app.storage.temporary-directory:temp/book}") String directory) {
        return new LocalTemporaryFileStorage(Path.of(directory));
    }

    @Bean("permanentFileStorage")
    FileStorage permanentFileStorage(
            @Value("${app.storage.permanent-directory:storage}") String directory,
            @Value("${app.storage.provider:local}") String provider,
            @Value("${app.storage.r2.endpoint:}") String endpoint,
            @Value("${app.storage.r2.access-key-id:}") String accessKey,
            @Value("${app.storage.r2.secret-access-key:}") String secretKey,
            @Value("${app.storage.r2.bucket:}") String bucket,
            @Value("${app.storage.r2.region:auto}") String region) {
        if ("r2".equalsIgnoreCase(provider)) {
            if (endpoint.isBlank() || accessKey.isBlank() || secretKey.isBlank() || bucket.isBlank()) {
                throw new IllegalStateException("R2 storage requires endpoint, credentials and bucket");
            }
            S3Client client = S3Client.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                    .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true)
                            .chunkedEncodingEnabled(false).build()).build();
            return new R2FileStorage(client, bucket);
        }
        return new LocalFileStorage(Path.of(directory));
    }

    @Bean
    PdfBoxTitleReader pdfBoxTitleReader() { return new PdfBoxTitleReader(); }

    @Bean
    TitleExtractor titleExtractor(PdfBoxTitleReader reader) {
        return new PdfBoxTitleExtractorAdapter(reader);
    }

    @Bean
    PdfBoxCoverRenderer pdfBoxCoverRenderer() { return new PdfBoxCoverRenderer(); }

    @Bean
    CoverGenerator coverGenerator(PdfBoxCoverRenderer renderer,
                                  @Value("${app.storage.cover-directory:temp/cover}") String directory) {
        return new PdfBoxCoverGeneratorAdapter(renderer, Path.of(directory));
    }

    @Bean
    ExtractBookTitle extractBookTitle(TitleExtractor extractor) {
        return new ExtractBookTitle(extractor);
    }

    @Bean
    GenerateBookCover generateBookCover(CoverGenerator generator) {
        return new GenerateBookCover(generator);
    }

    @Bean
    StoreCover storeCover(@Qualifier("permanentFileStorage") FileStorage storage) {
        return new StoreCover(storage);
    }

    @Bean
    StorePdf storePdf(@Qualifier("permanentFileStorage") FileStorage storage) {
        return new StorePdf(storage);
    }

    @Bean
    CoverProcessOrchestrator coverProcessOrchestrator(TemporaryFileStorage temporary,
                                                      GenerateBookCover generate,
                                                      StoreCover store) {
        return new CoverProcessOrchestrator(temporary, generate, store);
    }

    @Bean(destroyMethod = "close")
    ExecutorService bookUploadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    CreateBook createBook(klaus.biblioteca.application.port.BookRepository repository) {
        return new CreateBook(repository);
    }

    @Bean ListBooks listBooks(klaus.biblioteca.application.port.BookRepository repository) { return new ListBooks(repository); }
    @Bean GetBookByID getBookByID(klaus.biblioteca.application.port.BookRepository repository) { return new GetBookByID(repository); }
    @Bean GetBooksByTitle getBooksByTitle(klaus.biblioteca.application.port.BookRepository repository) { return new GetBooksByTitle(repository); }
    @Bean GetBooksByStatus getBooksByStatus(klaus.biblioteca.application.port.BookRepository repository) { return new GetBooksByStatus(repository); }
    @Bean UpdateBook updateBook(klaus.biblioteca.application.port.BookRepository repository) { return new UpdateBook(repository); }
    @Bean DeleteBook deleteBook(klaus.biblioteca.application.port.BookRepository repository,
                                @Qualifier("permanentFileStorage") FileStorage storage, Executor executor) {
        return new DeleteBook(repository, storage, executor);
    }

    @Bean
    BookUploadOrchestrator bookUploadOrchestrator(TemporaryFileStorage temporary,
                                                  ExtractBookTitle title,
                                                  CoverProcessOrchestrator cover,
                                                  StorePdf pdf,
                                                  CreateBook book,
                                                  Executor executor) {
        return new BookUploadOrchestrator(temporary, title, cover, pdf, book, executor);
    }
}
