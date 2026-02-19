package com.ecoimpact_360.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecoimpact_360.backend.dto.RankingDTO;
import com.ecoimpact_360.backend.service.RankingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ranking")
@RequiredArgsConstructor
public class RankingController {
    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<List<RankingDTO>> getRanking() {
        return ResponseEntity.ok(rankingService.getClassroomRanking());
    }
}