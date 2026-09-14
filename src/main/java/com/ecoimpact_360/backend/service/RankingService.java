package com.ecoimpact_360.backend.service;
import com.ecoimpact_360.backend.dto.RankingDTO;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.repository.WasteEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RankingService {
    private final WasteEntryRepository wasteEntryRepository;
    private final ImpactService impactService;
    public List<RankingDTO> getClassroomRankingForSchool(Long schoolId) {
        Map<String, List<WasteEntry>> byClassroom = wasteEntryRepository.findByClassroomSchoolId(schoolId).stream()
                .collect(Collectors.groupingBy(e -> e.getClassroom().getName()));
        return byClassroom.entrySet().stream()
                .map(entry -> {
                    List<WasteEntry> entries = entry.getValue();
                    double totalCo2 = entries.stream()
                            .mapToDouble(e -> e.getCo2Equivalent() != null ? e.getCo2Equivalent() : 0.0)
                            .sum();
                    double totalWaterSaved = entries.stream()
                            .mapToDouble(e -> impactService.calculateWaterSaved(e.getWasteType(), e.getQuantityKg()))
                            .sum();
                    return RankingDTO.builder()
                            .classroomName(entry.getKey())
                            .totalCo2(totalCo2)
                            .totalWaterSaved(totalWaterSaved)
                            .totalEntries((long) entries.size())
                            .build();
                })
                .sorted(Comparator.comparingDouble(RankingDTO::getTotalCo2).reversed())
                .collect(Collectors.toList());
    }
}
