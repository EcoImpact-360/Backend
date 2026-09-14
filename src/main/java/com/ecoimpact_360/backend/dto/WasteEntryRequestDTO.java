package com.ecoimpact_360.backend.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteEntryRequestDTO {
    @NotNull(message = "classroomId es obligatorio")
    private Long classroomId;
    @NotNull(message = "wasteTypeId es obligatorio")
    private Long wasteTypeId;
    @NotNull(message = "quantityKg es obligatorio")
    @Positive(message = "quantityKg debe ser mayor que 0")
    private Double quantityKg;
}
