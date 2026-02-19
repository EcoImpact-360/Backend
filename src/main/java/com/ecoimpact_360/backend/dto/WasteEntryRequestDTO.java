package com.ecoimpact_360.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteEntryRequestDTO {
    private Long classroomId;
    private Long wasteTypeId;
    private Double quantityKg;
}

