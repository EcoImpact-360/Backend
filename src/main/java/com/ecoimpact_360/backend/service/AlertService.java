package com.ecoimpact_360.backend.service;

import com.ecoimpact_360.backend.model.Alert;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.model.enums.AlertType;
import com.ecoimpact_360.backend.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    public void checkAndCreateAlert(WasteEntry entry) {
        Double maxAllowed = entry.getWasteType().getMaxKgPerWeek();
        
       
        if (maxAllowed != null && entry.getQuantityKg() > maxAllowed) {
            
        
            boolean alreadyHasAlert = alertRepository
                .existsByWasteTypeIdAndResolvedFalse(entry.getWasteType().getId());

            if (!alreadyHasAlert) {
                Alert alert = new Alert();
                alert.setWasteType(entry.getWasteType());
                alert.setClassroom(entry.getClassroom());
                alert.setAlertType(AlertType.THRESHOLD_EXCEEDED); 
                alert.setTotalKg(entry.getQuantityKg());
                alert.setResolved(false);
                alert.setCreatedAt(LocalDateTime.now());
                
                alertRepository.save(alert);
            }
        }
    }
}