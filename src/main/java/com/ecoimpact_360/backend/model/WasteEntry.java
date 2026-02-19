package com.ecoimpact_360.backend.model;

import java.time.LocalDateTime;

import com.ecoimpact_360.backend.model.enums.WasteStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "waste_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "waste_type_id")
    private WasteType wasteType;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @NotNull
    @Positive
    private Double quantityKg;
    private Double co2Equivalent;

    @Enumerated(EnumType.STRING)
    private WasteStatus status = WasteStatus.PENDING;
    private LocalDateTime createdAt;
}
