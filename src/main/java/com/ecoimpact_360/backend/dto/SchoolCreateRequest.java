package com.ecoimpact_360.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class SchoolCreateRequest {
    @NotBlank(message = "El nombre del colegio es obligatorio")
    private String name;
    private String city;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
