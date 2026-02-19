package com.ecoimpact_360.backend.service;

import com.ecoimpact_360.backend.dto.RankingDTO;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.repository.WasteEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final WasteEntryRepository wasteEntryRepository;

    public List<RankingDTO> getClassroomRanking() {
        return wasteEntryRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getClassroom().getName(),
                        Collectors.summingDouble(WasteEntry::getCo2Equivalent)))
                .entrySet().stream()
                .map(entry -> RankingDTO.builder()
                        .classroomName(entry.getKey())
                        .totalCo2(entry.getValue())
                        .build())
                .sorted(Comparator.comparingDouble(RankingDTO::getTotalCo2))
                .collect(Collectors.toList());
    }
}