package klaus.biblioteca.infra.web;

import klaus.biblioteca.application.exception.BookNotFoundException;
import klaus.biblioteca.infra.web.dto.ApiErrorResponse;
import klaus.biblioteca.infra.storage.local.MediaFileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final String INVALID_REQUEST = "Invalid request";
    private static final String UPLOAD_TOO_LARGE = "Uploaded file exceeds the 60 MB limit";
    private static final String INTERNAL_ERROR = "Internal server error";

    @ExceptionHandler(BookNotFoundException.class)
    ResponseEntity<ApiErrorResponse> notFound(BookNotFoundException e, HttpServletRequest request) {
        if (isExistingBookEndpoint(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(INTERNAL_ERROR));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ApiErrorResponse> uploadTooLarge(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new ApiErrorResponse(UPLOAD_TOO_LARGE));
    }

    @ExceptionHandler(MediaFileNotFoundException.class)
    ResponseEntity<ApiErrorResponse> mediaNotFound(MediaFileNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse("Media file not found"));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMediaTypeNotSupportedException.class,
            MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class, IllegalArgumentException.class,
            MethodArgumentNotValidException.class})
    ResponseEntity<ApiErrorResponse> badRequest(Exception e) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(INVALID_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(INTERNAL_ERROR));
    }

    private boolean isExistingBookEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String bookPath = uri.substring(contextPath.length());
        return ("GET".equals(request.getMethod()) || "PATCH".equals(request.getMethod())
                || "DELETE".equals(request.getMethod()))
                && bookPath.matches("/books/[^/]+(?:/(?:cover|pdf))?");
    }
}
