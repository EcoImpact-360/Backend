package com.ecoimpact_360.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomResponseDTO {
    private Long id;
    private String name;
    private Integer score;
    private Long schoolId;
    private String schoolName;
}
