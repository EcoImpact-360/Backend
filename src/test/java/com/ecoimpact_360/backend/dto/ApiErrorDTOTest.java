package com.ecoimpact_360.backend.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testAllArgsConstructor() {
        Instant timestamp = Instant.now();
        ApiErrorDTO dto = new ApiErrorDTO(
                timestamp,
                404,
                "Not Found",
                "Resource not found",
                "/api/schools/1",
                "RESOURCE_NOT_FOUND",
                Map.of("resource", "School")
        );

        assertEquals(timestamp, dto.getTimestamp());
        assertEquals(404, dto.getStatus());
        assertEquals("Not Found", dto.getError());
        assertEquals("Resource not found", dto.getMessage());
        assertEquals("/api/schools/1", dto.getPath());
        assertEquals("RESOURCE_NOT_FOUND", dto.getErrorCode());
        assertEquals(Map.of("resource", "School"), dto.getDetails());
    }

    @Test
    void testNoArgsConstructor() {
        ApiErrorDTO dto = new ApiErrorDTO();

        assertNull(dto.getTimestamp());
        assertEquals(0, dto.getStatus());
        assertNull(dto.getError());
        assertNull(dto.getMessage());
        assertNull(dto.getPath());
        assertNull(dto.getErrorCode());
        assertNull(dto.getDetails());
    }

    @Test
    void testSetters() {
        ApiErrorDTO dto = new ApiErrorDTO();
        Instant timestamp = Instant.now();

        dto.setTimestamp(timestamp);
        dto.setStatus(500);
        dto.setError("Internal Server Error");
        dto.setMessage("Something went wrong");
        dto.setPath("/api/test");
        dto.setErrorCode("INTERNAL_ERROR");
        dto.setDetails(Map.of("key", "value"));

        assertEquals(timestamp, dto.getTimestamp());
        assertEquals(500, dto.getStatus());
        assertEquals("Internal Server Error", dto.getError());
        assertEquals("Something went wrong", dto.getMessage());
        assertEquals("/api/test", dto.getPath());
        assertEquals("INTERNAL_ERROR", dto.getErrorCode());
        assertEquals(Map.of("key", "value"), dto.getDetails());
    }

    @Test
    void testJsonSerialization() throws Exception {
        Instant timestamp = Instant.parse("2024-01-15T10:30:00Z");
        ApiErrorDTO dto = new ApiErrorDTO(
                timestamp,
                400,
                "Bad Request",
                "Invalid input",
                "/api/schools",
                "VALIDATION_ERROR",
                Map.of("field", "name")
        );

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"timestamp\":\"2024-01-15T10:30:00Z\""));
        assertTrue(json.contains("\"status\":400"));
        assertTrue(json.contains("\"error\":\"Bad Request\""));
        assertTrue(json.contains("\"message\":\"Invalid input\""));
        assertTrue(json.contains("\"path\":\"/api/schools\""));
        assertTrue(json.contains("\"errorCode\":\"VALIDATION_ERROR\""));
        assertTrue(json.contains("\"details\""));
    }

    @Test
    void testJsonDeserialization() throws Exception {
        String json = """
                {
                    "timestamp": "2024-01-15T10:30:00Z",
                    "status": 404,
                    "error": "Not Found",
                    "message": "School not found",
                    "path": "/api/schools/999",
                    "errorCode": "RESOURCE_NOT_FOUND",
                    "details": {"resource": "School", "field": "id", "value": 999}
                }
                """;

        ApiErrorDTO dto = objectMapper.readValue(json, ApiErrorDTO.class);

        assertEquals(Instant.parse("2024-01-15T10:30:00Z"), dto.getTimestamp());
        assertEquals(404, dto.getStatus());
        assertEquals("Not Found", dto.getError());
        assertEquals("School not found", dto.getMessage());
        assertEquals("/api/schools/999", dto.getPath());
        assertEquals("RESOURCE_NOT_FOUND", dto.getErrorCode());
        assertNotNull(dto.getDetails());
    }

    @Test
    void testJsonIncludeNonNull() throws Exception {
        ApiErrorDTO dtoWithDetails = new ApiErrorDTO(
                Instant.now(),
                400,
                "Bad Request",
                "Error message",
                "/api/test",
                "BAD_REQUEST",
                Map.of("key", "value")
        );

        ApiErrorDTO dtoWithoutDetails = new ApiErrorDTO(
                Instant.now(),
                400,
                "Bad Request",
                "Error message",
                "/api/test",
                "BAD_REQUEST",
                null
        );

        String jsonWithDetails = objectMapper.writeValueAsString(dtoWithDetails);
        String jsonWithoutDetails = objectMapper.writeValueAsString(dtoWithoutDetails);

        assertTrue(jsonWithDetails.contains("\"details\""));
        assertFalse(jsonWithoutDetails.contains("\"details\":null"));
    }

    @Test
    void testEqualsAndHashCode() {
        Instant timestamp = Instant.now();
        ApiErrorDTO dto1 = new ApiErrorDTO(timestamp, 404, "Not Found", "msg", "/api", "CODE", null);
        ApiErrorDTO dto2 = new ApiErrorDTO(timestamp, 404, "Not Found", "msg", "/api", "CODE", null);
        ApiErrorDTO dto3 = new ApiErrorDTO(timestamp, 500, "Error", "msg", "/api", "CODE", null);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        ApiErrorDTO dto = new ApiErrorDTO(
                Instant.now(),
                404,
                "Not Found",
                "message",
                "/api/test",
                "CODE",
                Map.of("key", "value")
        );

        String str = dto.toString();

        assertTrue(str.contains("timestamp"));
        assertTrue(str.contains("status"));
        assertTrue(str.contains("404"));
        assertTrue(str.contains("errorCode"));
        assertTrue(str.contains("CODE"));
    }
}
