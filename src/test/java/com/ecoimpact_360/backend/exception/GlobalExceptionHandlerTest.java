package com.ecoimpact_360.backend.exception;

import com.ecoimpact_360.backend.dto.ApiErrorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/test");
    }

    @Test
    void handleResourceNotFound_Returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("School", "id", 1L);

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleResourceNotFound(ex, mockRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleResourceNotFound_ContainsCorrectErrorCode() {
        ResourceNotFoundException ex = new ResourceNotFoundException("School", "id", 1L);

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleResourceNotFound(ex, mockRequest);

        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
    }

    @Test
    void handleResourceNotFound_ContainsResourceDetails() {
        ResourceNotFoundException ex = new ResourceNotFoundException("School", "id", 1L);

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleResourceNotFound(ex, mockRequest);

        assertNotNull(response.getBody().getDetails());
        assertTrue(response.getBody().getDetails() instanceof java.util.Map);
        java.util.Map details = (java.util.Map) response.getBody().getDetails();
        assertEquals("School", details.get("resource"));
        assertEquals("id", details.get("field"));
        assertEquals(1L, details.get("value"));
    }

    @Test
    void handleResourceNotFound_Returns404StatusCode() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Classroom", "name", "A101");

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleResourceNotFound(ex, mockRequest);

        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleValidationErrors_Returns400() {
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, new BeanPropertyBindingResult(new Object(), "objectName"));

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleValidationErrors(ex, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleValidationErrors_ContainsFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "name", "Name is required"));
        bindingResult.addError(new FieldError("testObject", "quantity", "Quantity must be positive"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleValidationErrors(ex, mockRequest);

        assertNotNull(response.getBody().getDetails());
        assertTrue(response.getBody().getDetails() instanceof java.util.Map);
        java.util.Map details = (java.util.Map) response.getBody().getDetails();
        assertTrue(details.containsKey("name"));
        assertTrue(details.containsKey("quantity"));
    }

    @Test
    void handleValidationErrors_ContainsCorrectErrorCode() {
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, new BeanPropertyBindingResult(new Object(), "objectName"));

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleValidationErrors(ex, mockRequest);

        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
    }

    @Test
    void handleValidationErrors_Returns400StatusCode() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "field", "error message"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleValidationErrors(ex, mockRequest);

        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleMethodNotSupported_Returns405() throws Exception {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST", Set.of("GET", "PUT"));

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleMethodNotSupported(ex, mockRequest);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    void handleMethodNotSupported_ContainsSupportedMethods() throws Exception {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST", Set.of("GET", "PUT"));

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleMethodNotSupported(ex, mockRequest);

        assertNotNull(response.getBody().getDetails());
        java.util.Map details = (java.util.Map) response.getBody().getDetails();
        assertTrue(details.containsKey("supportedMethods"));
    }

    @Test
    void handleMethodNotSupported_ContainsCorrectErrorCode() throws Exception {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("DELETE", Set.of("GET", "POST"));

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleMethodNotSupported(ex, mockRequest);

        assertEquals("METHOD_NOT_ALLOWED", response.getBody().getErrorCode());
    }

    @Test
    void handleMediaTypeNotSupported_Returns415() throws Exception {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("application/xml", List.of(org.springframework.http.MediaType.APPLICATION_JSON));

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleMediaTypeNotSupported(ex, mockRequest);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }

    @Test
    void handleMediaTypeNotSupported_ContainsContentTypeDetails() throws Exception {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("application/xml", List.of(org.springframework.http.MediaType.APPLICATION_JSON));

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleMediaTypeNotSupported(ex, mockRequest);

        assertNotNull(response.getBody().getDetails());
        java.util.Map details = (java.util.Map) response.getBody().getDetails();
        assertTrue(details.containsKey("contentType"));
        assertTrue(details.containsKey("supportedTypes"));
    }

    @Test
    void handleMediaTypeNotSupported_ContainsCorrectErrorCode() throws Exception {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("text/plain");

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleMediaTypeNotSupported(ex, mockRequest);

        assertEquals("UNSUPPORTED_MEDIA_TYPE", response.getBody().getErrorCode());
    }

    @Test
    void handleMessageNotReadable_Returns400() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON");

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleMessageNotReadable(ex, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleMessageNotReadable_ContainsCorrectErrorCode() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON");

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleMessageNotReadable(ex, mockRequest);

        assertEquals("BAD_REQUEST", response.getBody().getErrorCode());
    }

    @Test
    void handleMessageNotReadable_ContainsCause() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON format");

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleMessageNotReadable(ex, mockRequest);

        assertNotNull(response.getBody().getDetails());
        java.util.Map details = (java.util.Map) response.getBody().getDetails();
        assertTrue(details.containsKey("cause"));
    }

    @Test
    void handleGenericException_Returns500() {
        Exception ex = new RuntimeException("Something went wrong");

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleGenericException(ex, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleGenericException_ContainsCorrectErrorCode() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleGenericException(ex, mockRequest);

        assertEquals("INTERNAL_ERROR", response.getBody().getErrorCode());
    }

    @Test
    void handleGenericException_ContainsExceptionType() {
        Exception ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleGenericException(ex, mockRequest);

        assertNotNull(response.getBody().getDetails());
        java.util.Map details = (java.util.Map) response.getBody().getDetails();
        assertTrue(details.containsKey("exceptionType"));
    }

    @Test
    void handleGenericException_ContainsRootCause() {
        Exception ex = new RuntimeException("Root cause message");

        ResponseEntity<ApiErrorDTO> response = globalExceptionHandler.handleGenericException(ex, mockRequest);

        assertNotNull(response.getBody().getDetails());
        java.util.Map details = (java.util.Map) response.getBody().getDetails();
        assertTrue(details.containsKey("rootCause"));
    }

    @Test
    void allResponsesContainTimestamp() {
        mockRequest.setRequestURI("/api/resource");

        ResponseEntity<ApiErrorDTO> notFound = globalExceptionHandler.handleResourceNotFound(
                new ResourceNotFoundException("School", "id", 1L), mockRequest);
        ResponseEntity<ApiErrorDTO> validation = globalExceptionHandler.handleValidationErrors(
                new MethodArgumentNotValidException(null, new BeanPropertyBindingResult(new Object(), "obj")), mockRequest);
        ResponseEntity<ApiErrorDTO> generic = globalExceptionHandler.handleGenericException(
                new Exception("Error"), mockRequest);

        assertNotNull(notFound.getBody().getTimestamp());
        assertNotNull(validation.getBody().getTimestamp());
        assertNotNull(generic.getBody().getTimestamp());
    }

    @Test
    void allResponsesContainPath() {
        mockRequest.setRequestURI("/api/custom-path");

        ResponseEntity<ApiErrorDTO> notFound = globalExceptionHandler.handleResourceNotFound(
                new ResourceNotFoundException("School", "id", 1L), mockRequest);
        ResponseEntity<ApiErrorDTO> validation = globalExceptionHandler.handleValidationErrors(
                new MethodArgumentNotValidException(null, new BeanPropertyBindingResult(new Object(), "obj")), mockRequest);

        assertEquals("/api/custom-path", notFound.getBody().getPath());
        assertEquals("/api/custom-path", validation.getBody().getPath());
    }

    @Test
    void errorResponseBodyIsNeverNull() {
        ResponseEntity<ApiErrorDTO> notFound = globalExceptionHandler.handleResourceNotFound(
                new ResourceNotFoundException("School", "id", 1L), mockRequest);
        ResponseEntity<ApiErrorDTO> validation = globalExceptionHandler.handleValidationErrors(
                new MethodArgumentNotValidException(null, new BeanPropertyBindingResult(new Object(), "obj")), mockRequest);
        ResponseEntity<ApiErrorDTO> methodNotAllowed = globalExceptionHandler.handleMethodNotSupported(
                new HttpRequestMethodNotSupportedException("POST"), mockRequest);
        ResponseEntity<ApiErrorDTO> generic = globalExceptionHandler.handleGenericException(
                new Exception("Error"), mockRequest);

        assertNotNull(notFound.getBody());
        assertNotNull(validation.getBody());
        assertNotNull(methodNotAllowed.getBody());
        assertNotNull(generic.getBody());
    }
}
