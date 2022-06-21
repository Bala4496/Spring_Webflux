package ua.bala.reactive.globalerrorhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.bala.reactive.exception.MoviesInfoClientException;
import ua.bala.reactive.exception.MoviesInfoServerException;

@Component
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(MoviesInfoClientException.class)
    public ResponseEntity<String> handleClientException(MoviesInfoClientException exception) {
        log.error("Exception Caught in handleClientException: {}", exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }

    @ExceptionHandler(MoviesInfoServerException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception) {
        log.error("Exception Caught in handleClientException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
