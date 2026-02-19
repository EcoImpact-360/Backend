package com.ecoimpact_360.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecoimpact_360.backend.dto.DashboardDTO;
import com.ecoimpact_360.backend.services.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/global")
    public ResponseEntity<DashboardDTO> getGlobalDashboard() {
        return ResponseEntity.ok(dashboardService.getGlobalStats());
    }

    @GetMapping("/classroom/{id}")
    public ResponseEntity<DashboardDTO> getClassroomDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(dashboardService.getClassroomStats(id));
    }
}
