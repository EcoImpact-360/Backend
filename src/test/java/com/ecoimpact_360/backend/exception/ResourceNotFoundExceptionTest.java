package com.ecoimpact_360.backend.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void testConstructorWithResourceFieldValue() {
        ResourceNotFoundException ex = new ResourceNotFoundException("School", "id", 1L);

        assertEquals("School no encontrado con id: '1'", ex.getMessage());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        assertEquals("School", ex.getResourceName());
        assertEquals("id", ex.getFieldName());
        assertEquals(1L, ex.getFieldValue());
    }

    @Test
    void testConstructorWithStringFieldValue() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Classroom", "name", "A101");

        assertEquals("Classroom no encontrado con name: 'A101'", ex.getMessage());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        assertEquals("Classroom", ex.getResourceName());
        assertEquals("name", ex.getFieldName());
        assertEquals("A101", ex.getFieldValue());
    }

    @Test
    void testConstructorWithCustomMessage() {
        String customMessage = "Entidad personalizada no encontrada";
        ResourceNotFoundException ex = new ResourceNotFoundException(customMessage);

        assertEquals(customMessage, ex.getMessage());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        assertNull(ex.getResourceName());
        assertNull(ex.getFieldName());
        assertNull(ex.getFieldValue());
    }

    @Test
    void testErrorCodeIsNotNull() {
        ResourceNotFoundException ex = new ResourceNotFoundException("WasteEntry", "id", 100L);

        assertNotNull(ex.getErrorCode());
        assertEquals("RESOURCE_NOT_FOUND", ex.getErrorCode().getCode());
        assertEquals("Recurso no encontrado", ex.getErrorCode().getDescription());
    }

    @Test
    void testDifferentResourceTypes() {
        ResourceNotFoundException schoolEx = new ResourceNotFoundException("School", "id", 1L);
        ResourceNotFoundException classroomEx = new ResourceNotFoundException("Classroom", "id", 2L);
        ResourceNotFoundException wasteEntryEx = new ResourceNotFoundException("WasteEntry", "id", 3L);

        assertEquals("School no encontrado con id: '1'", schoolEx.getMessage());
        assertEquals("Classroom no encontrado con id: '2'", classroomEx.getMessage());
        assertEquals("WasteEntry no encontrado con id: '3'", wasteEntryEx.getMessage());
    }

    @Test
    void testNullFieldValue() {
        ResourceNotFoundException ex = new ResourceNotFoundException("School", "id", null);

        assertEquals("School no encontrado con id: 'null'", ex.getMessage());
        assertNull(ex.getFieldValue());
    }
}
