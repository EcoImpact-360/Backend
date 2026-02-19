package com.ecoimpact_360.backend.exception;

import com.ecoimpact_360.backend.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        logger.error("Error: {}, ErrorCode: {}, Message: {}, Details: {Resource: {}, Field: {}, Value: {}}",
                ex.getClass().getSimpleName(),
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                ex.getResourceName(),
                ex.getFieldName(),
                ex.getFieldValue());

        ApiErrorDTO error = new ApiErrorDTO(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode().getCode(),
                Map.of(
                        "resource", ex.getResourceName() != null ? ex.getResourceName() : "unknown",
                        "field", ex.getFieldName() != null ? ex.getFieldName() : "unknown",
                        "value", ex.getFieldValue() != null ? ex.getFieldValue() : "unknown"
                )
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        logger.error("Error: {}, ErrorCode: {}, Message: {}, Details: {}",
                ex.getClass().getSimpleName(),
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Error de validación en campos",
                fieldErrors);

        ApiErrorDTO error = new ApiErrorDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Error de validación en campos",
                request.getRequestURI(),
                ErrorCode.VALIDATION_ERROR.getCode(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        logger.error("Error: {}, ErrorCode: {}, Message: {}, Details: {SupportedMethods: {}}",
                ex.getClass().getSimpleName(),
                ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                ex.getMessage(),
                ex.getSupportedMethods());

        ApiErrorDTO error = new ApiErrorDTO(
                Instant.now(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                ex.getMessage(),
                request.getRequestURI(),
                ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                Map.of("supportedMethods", ex.getSupportedMethods() != null ? ex.getSupportedMethods() : "unknown")
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorDTO> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        logger.error("Error: {}, ErrorCode: {}, Message: {}, Details: {ContentType: {}, SupportedTypes: {}}",
                ex.getClass().getSimpleName(),
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(),
                ex.getMessage(),
                ex.getContentType(),
                ex.getSupportedMediaTypes());

        ApiErrorDTO error = new ApiErrorDTO(
                Instant.now(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "Unsupported Media Type",
                ex.getMessage(),
                request.getRequestURI(),
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(),
                Map.of(
                        "contentType", ex.getContentType() != null ? ex.getContentType() : "unknown",
                        "supportedTypes", ex.getSupportedMediaTypes()
                )
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        logger.error("Error: {}, ErrorCode: {}, Message: {}, Details: {}",
                ex.getClass().getSimpleName(),
                ErrorCode.BAD_REQUEST.getCode(),
                "Cuerpo de solicitud inválido o malformado",
                ex.getMessage());

        ApiErrorDTO error = new ApiErrorDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Cuerpo de solicitud inválido o malformado",
                request.getRequestURI(),
                ErrorCode.BAD_REQUEST.getCode(),
                Map.of("cause", ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Error: {}, ErrorCode: {}, Message: {}, Details: {ExceptionType: {}, StackTrace: {}}",
                ex.getClass().getSimpleName(),
                ErrorCode.INTERNAL_ERROR.getCode(),
                "Error interno del servidor",
                ex.getClass().getName(),
                getStackTraceSnippet(ex));

        ApiErrorDTO error = new ApiErrorDTO(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Error interno del servidor",
                request.getRequestURI(),
                ErrorCode.INTERNAL_ERROR.getCode(),
                Map.of(
                        "exceptionType", ex.getClass().getSimpleName(),
                        "rootCause", ex.getMessage() != null ? ex.getMessage() : "Unknown"
                )
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String getStackTraceSnippet(Exception ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(3, stackTrace.length); i++) {
                sb.append(stackTrace[i].toString()).append("; ");
            }
            return sb.toString();
        }
        return "No stack trace available";
    }
}
