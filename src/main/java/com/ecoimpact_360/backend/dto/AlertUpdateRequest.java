package com.ecoimpact_360.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
@Data
public class AlertUpdateRequest {
    @NotBlank(message = "El título de la alerta es obligatorio")
    private String title;
    private String message;
    private Long wasteTypeId;
    private String alertType;
    @PositiveOrZero(message = "El total en kg no puede ser negativo")
    private Double totalKg;
}
