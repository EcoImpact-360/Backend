package com.ecoimpact_360.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
@Data
public class ClassroomCreateRequest {
    @NotBlank(message = "El nombre del aula es obligatorio")
    private String name;
    @PositiveOrZero(message = "La puntuación no puede ser negativa")
    private Integer score;
}
