package klaus.biblioteca.external.r2;

import klaus.biblioteca.application.model.StoredFile;
import klaus.biblioteca.application.port.FileStorage;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class R2FileStorage implements FileStorage {
    private final S3Client client;
    private final String bucket;

    public R2FileStorage(S3Client client, String bucket) {
        this.client = Objects.requireNonNull(client);
        this.bucket = Objects.requireNonNull(bucket);
    }

    @Override
    public StoredFile store(InputStream inputStream, String fileName) {
        try {
            byte[] content = inputStream.readAllBytes();
            client.putObject(PutObjectRequest.builder().bucket(bucket).key(fileName)
                    .contentType(contentType(fileName)).build(), RequestBody.fromBytes(content));
            return new StoredFile(fileName);
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Could not store file", exception);
        }
    }

    @Override
    public InputStream retrieve(String path) {
        try {
            return client.getObject(GetObjectRequest.builder().bucket(bucket).key(path).build());
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Could not retrieve file", exception);
        }
    }

    @Override
    public void delete(String path) {
        try {
            client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(path).build());
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Could not delete file", exception);
        }
    }

    private String contentType(String fileName) {
        String normalized = fileName.toLowerCase();
        return normalized.endsWith(".png") || normalized.endsWith("/cover")
                ? "image/png" : "application/pdf";
    }
}
