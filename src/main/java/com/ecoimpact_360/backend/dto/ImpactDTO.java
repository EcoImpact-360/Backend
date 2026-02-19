package com.ecoimpact_360.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImpactDTO {
    private Double co2Kg;
    private Double waterSavedLiters;
    private Double treesEquivalent;
    private Double kmCarEquivalent;
}