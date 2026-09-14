package com.ecoimpact_360.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponseDTO {
    private Long id;
    private Long classroomId;
    private String classroomName;
    private Long wasteTypeId;
    private String wasteTypeName;
    private String alertType;
    private Double totalKg;
    private Boolean resolved;
    private LocalDateTime createdAt;
}
