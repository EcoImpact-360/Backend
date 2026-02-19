package com.ecoimpact_360.backend.controller;

import com.ecoimpact_360.backend.model.Alert;
import com.ecoimpact_360.backend.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRepository alertRepository;

   
    @GetMapping("/pending")
    public ResponseEntity<List<Alert>> getPendingAlerts() {
        return ResponseEntity.ok(alertRepository.findByResolvedFalse());
    }

   
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveAlert(@PathVariable Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found with ID: " + id));
        
        alert.setResolved(true);
        alertRepository.save(alert);
        
        return ResponseEntity.noContent().build();
    }

   
    @GetMapping("/history")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }
}