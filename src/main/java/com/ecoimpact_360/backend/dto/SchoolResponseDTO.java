package com.ecoimpact_360.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolResponseDTO {
    private Long id;
    private String name;
    private String city;
}
