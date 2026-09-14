package com.ecoimpact_360.backend.dto;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
@Data
public class AlertUpdateRequest {
    private String title;
    private String message;
    private String alertType;
    @PositiveOrZero(message = "El total en kg no puede ser negativo")
    private Double totalKg;
}
