package com.ecoimpact_360.backend.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder 
public class WasteEntryResponseDTO {
    private Long id;
    private String wasteTypeName;
    private Double quantityKg;
    
    private Double co2Kg;
    private Double waterSaved;
    private Double treesEquivalent;
    private Double kmCarEquivalent;
}