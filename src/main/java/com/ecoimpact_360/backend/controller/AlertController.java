package com.ecoimpact_360.backend.controller;
import com.ecoimpact_360.backend.dto.AlertResponseDTO;
import com.ecoimpact_360.backend.security.AuthInterceptor;
import com.ecoimpact_360.backend.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;
    @GetMapping("/pending")
    public ResponseEntity<List<AlertResponseDTO>> getPendingAlerts(@RequestAttribute(AuthInterceptor.SCHOOL_ID_ATTRIBUTE) Long schoolId) {
        return ResponseEntity.ok(alertService.getPendingAlertsForSchool(schoolId));
    }
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveAlert(@PathVariable Long id,
                                              @RequestAttribute(AuthInterceptor.SCHOOL_ID_ATTRIBUTE) Long schoolId) {
        alertService.resolveAlert(id, schoolId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/history")
    public ResponseEntity<List<AlertResponseDTO>> getAllAlerts(@RequestAttribute(AuthInterceptor.SCHOOL_ID_ATTRIBUTE) Long schoolId) {
        return ResponseEntity.ok(alertService.getAllAlertsForSchool(schoolId));
    }
}
