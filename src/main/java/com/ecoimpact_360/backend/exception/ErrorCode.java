package com.ecoimpact_360.backend.exception;

public enum ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR", "Error de validación"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Recurso no encontrado"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Error interno del servidor"),
    BAD_REQUEST("BAD_REQUEST", "Solicitud incorrecta"),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "Método no permitido"),
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", "Tipo de medio no soportado");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
