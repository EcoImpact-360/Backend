package com.ecoimpact_360.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecoimpact_360.backend.model.WasteType;

@Repository
public interface WasteTypeRepository extends JpaRepository<WasteType, Long> {
}