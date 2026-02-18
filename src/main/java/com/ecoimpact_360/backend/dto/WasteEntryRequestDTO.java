package com.ecoimpact_360.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WasteEntryRequestDTO(
    @NotNull Long classroomId,
    @NotNull Long wasteTypeId,
    @Positive Double quantityKg,
    String observation
) {}




