package com.ecoimpact_360.backend.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorCodeTest {

    @Test
    void testValidationErrorCode() {
        ErrorCode code = ErrorCode.VALIDATION_ERROR;

        assertEquals("VALIDATION_ERROR", code.getCode());
        assertEquals("Error de validación", code.getDescription());
    }

    @Test
    void testResourceNotFoundCode() {
        ErrorCode code = ErrorCode.RESOURCE_NOT_FOUND;

        assertEquals("RESOURCE_NOT_FOUND", code.getCode());
        assertEquals("Recurso no encontrado", code.getDescription());
    }

    @Test
    void testInternalErrorCode() {
        ErrorCode code = ErrorCode.INTERNAL_ERROR;

        assertEquals("INTERNAL_ERROR", code.getCode());
        assertEquals("Error interno del servidor", code.getDescription());
    }

    @Test
    void testBadRequestCode() {
        ErrorCode code = ErrorCode.BAD_REQUEST;

        assertEquals("BAD_REQUEST", code.getCode());
        assertEquals("Solicitud incorrecta", code.getDescription());
    }

    @Test
    void testMethodNotAllowedCode() {
        ErrorCode code = ErrorCode.METHOD_NOT_ALLOWED;

        assertEquals("METHOD_NOT_ALLOWED", code.getCode());
        assertEquals("Método no permitido", code.getDescription());
    }

    @Test
    void testUnsupportedMediaTypeCode() {
        ErrorCode code = ErrorCode.UNSUPPORTED_MEDIA_TYPE;

        assertEquals("UNSUPPORTED_MEDIA_TYPE", code.getCode());
        assertEquals("Tipo de medio no soportado", code.getDescription());
    }

    @Test
    void testAllErrorCodesExist() {
        ErrorCode[] codes = ErrorCode.values();

        assertEquals(6, codes.length);
    }

    @Test
    void testErrorCodesAreNotNull() {
        for (ErrorCode code : ErrorCode.values()) {
            assertNotNull(code.getCode());
            assertNotNull(code.getDescription());
            assertFalse(code.getCode().isEmpty());
            assertFalse(code.getDescription().isEmpty());
        }
    }

    @Test
    void testErrorCodeValueOf() {
        assertEquals(ErrorCode.VALIDATION_ERROR, ErrorCode.valueOf("VALIDATION_ERROR"));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ErrorCode.valueOf("RESOURCE_NOT_FOUND"));
        assertEquals(ErrorCode.INTERNAL_ERROR, ErrorCode.valueOf("INTERNAL_ERROR"));
        assertEquals(ErrorCode.BAD_REQUEST, ErrorCode.valueOf("BAD_REQUEST"));
        assertEquals(ErrorCode.METHOD_NOT_ALLOWED, ErrorCode.valueOf("METHOD_NOT_ALLOWED"));
        assertEquals(ErrorCode.UNSUPPORTED_MEDIA_TYPE, ErrorCode.valueOf("UNSUPPORTED_MEDIA_TYPE"));
    }
}
