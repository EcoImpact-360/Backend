package com.ecoimpact_360.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorDTO {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;
    private Object details;
}
