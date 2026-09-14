package com.ecoimpact_360.backend.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class LoginRequest {
    @NotBlank(message = "El nombre del colegio es obligatorio")
    private String name;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
