package com.nutech.digitalservice.exception;

import com.nutech.digitalservice.dto.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        // Handle other validation errors - all validation errors get status 102
        String firstErrorMessage = errors.values().iterator().next();
        WebResponse<Object> response = WebResponse.builder()
                .status(102)
                .message(firstErrorMessage)
                .data(null)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<WebResponse<Object>> handleJwtValidationException(JwtValidationException ex) {
        WebResponse<Object> response = WebResponse.builder()
                .status(108)
                .message("Token tidak tidak valid atau kadaluwarsa")
                .data(null)
                .build();

        return ResponseEntity.status(401).body(response);
    }

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<WebResponse<Object>> handleFileValidationException(FileValidationException ex) {
        WebResponse<Object> response = WebResponse.builder()
                .status(102)
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<WebResponse<Object>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        WebResponse<Object> response = WebResponse.builder()
                .status(102)
                .message("Ukuran file terlalu besar. Maksimal 5MB")
                .data(null)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<Object>> handleGenericException(Exception ex) {
        // Handle file size related exceptions
        if (ex.getMessage() != null && ex.getMessage().contains("Maximum upload size exceeded")) {
            WebResponse<Object> response = WebResponse.builder()
                    .status(102)
                    .message("Ukuran file terlalu besar. Maksimal 5MB")
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }

        // Handle other unexpected exceptions
        WebResponse<Object> response = WebResponse.builder()
                .status(500)
                .message("Terjadi kesalahan server: " + ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<WebResponse<Object>> handleRuntimeException(RuntimeException ex) {
        int status = 102;
        String message = ex.getMessage();

        if (ex.getMessage().contains("Username atau password salah")) {
            status = 103;
            return ResponseEntity.status(401).body(WebResponse.builder()
                    .status(status)
                    .message(message)
                    .data(null)
                    .build());
        } else if (ex.getMessage().contains("Token tidak tidak valid atau kadaluwarsa")) {
            status = 108;
            return ResponseEntity.status(401).body(WebResponse.builder()
                    .status(status)
                    .message(message)
                    .data(null)
                    .build());
        } else if (ex.getMessage().contains("Saldo tidak mencukupi")) {
            status = 103;
        }

        // All other runtime errors (including validation errors) get status 102
        WebResponse<Object> response = WebResponse.builder()
                .status(status)
                .message(message)
                .data(null)
                .build();

        return ResponseEntity.badRequest().body(response);
    }
}