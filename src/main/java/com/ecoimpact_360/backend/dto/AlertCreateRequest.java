package com.ecoimpact_360.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
@Data
public class AlertCreateRequest {
    @NotNull(message = "El aula es obligatoria")
    private Long classroomId;
    private Long wasteTypeId;
    @NotBlank(message = "El título de la alerta es obligatorio")
    private String title;
    private String message;
    private String alertType;
    @PositiveOrZero(message = "El total en kg no puede ser negativo")
    private Double totalKg;
}
