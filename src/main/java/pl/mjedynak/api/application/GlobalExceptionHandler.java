package pl.mjedynak.api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.NotAcceptableStatusException;
import pl.mjedynak.api.domain.exception.RepositoriesNotFoundException;
import pl.mjedynak.api.domain.model.ErrorResponse;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(RepositoriesNotFoundException.class)
    public ResponseEntity<Mono<ErrorResponse>> handleRepositoryNotFoundException(RepositoriesNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Mono.just(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage())));
    }

    @ExceptionHandler(NotAcceptableStatusException.class)
    public ResponseEntity<Mono<String>> handleNotAcceptableStatusException(NotAcceptableStatusException e)
            throws JsonProcessingException {
        // need to manually convert as exception handling is not picked up for this exception in case of
        // automatic conversion
        var json =
                objectMapper.writeValueAsString(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Mono.just(json));
    }
}
