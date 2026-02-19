package com.ecoimpact_360.backend.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
        this.errorCode = ErrorCode.RESOURCE_NOT_FOUND;
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorCode = ErrorCode.RESOURCE_NOT_FOUND;
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
