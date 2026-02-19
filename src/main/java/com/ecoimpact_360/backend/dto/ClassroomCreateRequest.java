package com.ecoimpact_360.backend.dto;

import lombok.Data;

@Data
public class ClassroomCreateRequest {
    private String name;
    private Integer score;
    private Long schoolId;
}
