package com.ecoimpact_360.backend.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecoimpact_360.backend.dto.DashboardDTO;
import com.ecoimpact_360.backend.security.AuthInterceptor;
import com.ecoimpact_360.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    @GetMapping("/global")
    public ResponseEntity<DashboardDTO> getGlobalDashboard(@RequestAttribute(AuthInterceptor.SCHOOL_ID_ATTRIBUTE) Long schoolId) {
        return ResponseEntity.ok(dashboardService.getGlobalStats(schoolId));
    }
    @GetMapping("/classroom/{id}")
    public ResponseEntity<DashboardDTO> getClassroomDashboard(@PathVariable Long id,
                                                                @RequestAttribute(AuthInterceptor.SCHOOL_ID_ATTRIBUTE) Long schoolId) {
        return ResponseEntity.ok(dashboardService.getClassroomStats(id, schoolId));
    }
}
