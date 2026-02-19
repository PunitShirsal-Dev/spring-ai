package com.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.exception.*;
import org.springframework.ai.utility.CustomDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.ai.utility.AiConstants.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String EXCEPTION_OCCURRED = "Exception Occurred";

    private static @NonNull Result getResult() {
        int status = 0;
        HttpStatus httpStatus = null;
        if (HttpStatus.BAD_REQUEST.is4xxClientError()) {
            status = HttpStatus.BAD_REQUEST.value();
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (HttpStatus.NON_AUTHORITATIVE_INFORMATION.is5xxServerError()) {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (HttpStatus.FORBIDDEN.is4xxClientError()) {
            status = HttpStatus.FORBIDDEN.value();
            httpStatus = HttpStatus.FORBIDDEN;
        }
        return new Result(status, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });
        return buildResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED, fieldErrors);
    }

    @ExceptionHandler(AdminCustomException.class)
    public ResponseEntity<CustomDto.ErrorResponse> handleAdminCustomException(AdminCustomException ex, WebRequest request) {
        Result result = getResult();
        CustomDto.ErrorResponse response = new CustomDto.ErrorResponse(
                result.status(),
                ex.getMessage(),
                EXCEPTION_OCCURRED,
                request.getDescription(false).replace(URI, REPLACEMENT),
                LocalDateTime.now()
        );
        assert result.httpStatus() != null;
        return ResponseEntity.status(result.httpStatus()).body(response);
    }

    @ExceptionHandler(AuthCustomException.class)
    public ResponseEntity<CustomDto.ErrorResponse> handleAuthCustomException(AuthCustomException ex, WebRequest request) {
        Result result = getResult();
        CustomDto.ErrorResponse response = new CustomDto.ErrorResponse(
                result.status(),
                ex.getMessage(),
                EXCEPTION_OCCURRED,
                request.getDescription(false).replace(URI, REPLACEMENT),
                LocalDateTime.now()
        );
        assert result.httpStatus() != null;
        return ResponseEntity.status(result.httpStatus()).body(response);
    }

    @ExceptionHandler(ChatCustomException.class)
    public ResponseEntity<CustomDto.ErrorResponse> handleChatCustomException(ChatCustomException ex, WebRequest request) {
        Result result = getResult();
        CustomDto.ErrorResponse response = new CustomDto.ErrorResponse(
                result.status(),
                ex.getMessage(),
                EXCEPTION_OCCURRED,
                request.getDescription(false).replace(URI, REPLACEMENT),
                LocalDateTime.now()
        );
        assert result.httpStatus() != null;
        return ResponseEntity.status(result.httpStatus()).body(response);
    }

    @ExceptionHandler(EmbeddingCustomException.class)
    public ResponseEntity<CustomDto.ErrorResponse> handleEmbeddingCustomException(EmbeddingCustomException ex, WebRequest request) {
        Result result = getResult();
        CustomDto.ErrorResponse response = new CustomDto.ErrorResponse(
                result.status(),
                ex.getMessage(),
                EXCEPTION_OCCURRED,
                request.getDescription(false).replace(URI, REPLACEMENT),
                LocalDateTime.now()
        );
        assert result.httpStatus() != null;
        return ResponseEntity.status(result.httpStatus()).body(response);
    }

    @ExceptionHandler(RagCustomException.class)
    public ResponseEntity<CustomDto.ErrorResponse> handleRagCustomException(RagCustomException ex, WebRequest request) {
        Result result = getResult();
        CustomDto.ErrorResponse response = new CustomDto.ErrorResponse(
                result.status(),
                ex.getMessage(),
                EXCEPTION_OCCURRED,
                request.getDescription(false).replace(URI, REPLACEMENT),
                LocalDateTime.now()
        );
        assert result.httpStatus() != null;
        return ResponseEntity.status(result.httpStatus()).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, INVALID_USERNAME_OR_PASSWORD, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, AN_UNEXPECTED_ERROR_OCCURRED, null);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, Object details) {
        Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now().toString());
        body.put(STATUS, status.value());
        body.put(ERROR, status.getReasonPhrase());
        body.put(MESSAGE, message);
        if (details != null) body.put(DETAILS, details);
        return ResponseEntity.status(status).body(body);
    }

    private record Result(int status, HttpStatus httpStatus) {
    }
}
