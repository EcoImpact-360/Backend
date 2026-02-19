package com.ecoimpact_360.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecoimpact_360.backend.model.Alert;
import com.ecoimpact_360.backend.model.enums.AlertType;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByResolvedFalse();
    List<Alert> findByAlertType(AlertType alertType);
    boolean existsByWasteTypeIdAndResolvedFalse(Long wasteTypeId);
    
    long countByResolvedFalse();
}