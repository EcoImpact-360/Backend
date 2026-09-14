package com.ecoimpact_360.backend.service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.ecoimpact_360.backend.dto.DashboardDTO;
import com.ecoimpact_360.backend.dto.DashboardDTO.ClassroomRankingDTO;
import com.ecoimpact_360.backend.model.enums.WasteCategory;
import com.ecoimpact_360.backend.repository.AlertRepository;
import com.ecoimpact_360.backend.repository.ClassroomRepository;
import com.ecoimpact_360.backend.repository.WasteEntryRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final WasteEntryRepository wasteEntryRepository;
    private final AlertRepository alertRepository;
    private final ClassroomRepository classroomRepository;
    private final ClassRoomService classRoomService;
    private final ImpactService impactService;
    public DashboardDTO getGlobalStats(Long schoolId) {
        Double totalKg = wasteEntryRepository.sumKgBySchool(schoolId) != null ? wasteEntryRepository.sumKgBySchool(schoolId) : 0.0;
        Double totalCo2 = wasteEntryRepository.sumCo2BySchool(schoolId) != null ? wasteEntryRepository.sumCo2BySchool(schoolId) : 0.0;
        Double aguaAhorrada = calculateAguaAhorradaSchool(schoolId);
        Double arbresEquivalentes = impactService.calculateTreesEquivalent(totalCo2);
        Double kmCarroEquivalente = impactService.calculateKmCarEquivalent(totalCo2);
        long alertasActivas = alertRepository.countByResolvedFalseAndClassroomSchoolId(schoolId);
        List<ClassroomRankingDTO> ranking = getRankingAulasForSchool(schoolId);
        Map<String, Double> residuosPorCategoria = getResiduosPorCategoriaSchool(schoolId);
        return DashboardDTO.builder()
                .totalKgRecolectados(totalKg)
                .totalCo2Equivalente(totalCo2)
                .totalAguaAhorrada(aguaAhorrada)
                .arbolesEquivalentes(arbresEquivalentes)
                .kmCarroEquivalente(kmCarroEquivalente)
                .totalAlertasActivas(alertasActivas)
                .rankingAulas(ranking)
                .residuosPorCategoria(residuosPorCategoria)
                .build();
    }
    public DashboardDTO getClassroomStats(Long classroomId, Long schoolId) {
        classRoomService.getOwnedClassroomOrThrow(classroomId, schoolId);
        Double totalKg = wasteEntryRepository.sumKgByClassroom(classroomId) != null
                ? wasteEntryRepository.sumKgByClassroom(classroomId) : 0.0;
        Double totalCo2 = wasteEntryRepository.sumCo2ByClassroom(classroomId) != null
                ? wasteEntryRepository.sumCo2ByClassroom(classroomId) : 0.0;
        Double aguaAhorrada = calculateAguaAhorradaClassroom(classroomId);
        Double arbresEquivalentes = impactService.calculateTreesEquivalent(totalCo2);
        Double kmCarroEquivalente = impactService.calculateKmCarEquivalent(totalCo2);
        List<ClassroomRankingDTO> ranking = getRankingAulasForSchool(schoolId);
        Map<String, Double> residuosPorCategoria = getResiduosPorCategoriaClassroom(classroomId);
        return DashboardDTO.builder()
                .totalKgRecolectados(totalKg)
                .totalCo2Equivalente(totalCo2)
                .totalAguaAhorrada(aguaAhorrada)
                .arbolesEquivalentes(arbresEquivalentes)
                .kmCarroEquivalente(kmCarroEquivalente)
                .totalAlertasActivas(0L)
                .rankingAulas(ranking)
                .residuosPorCategoria(residuosPorCategoria)
                .build();
    }
    private List<ClassroomRankingDTO> getRankingAulasForSchool(Long schoolId) {
        return classroomRepository.findBySchoolIdOrderByScoreDesc(schoolId).stream()
                .map(c -> ClassroomRankingDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .score(c.getScore())
                        .build())
                .collect(Collectors.toList());
    }
    private Map<String, Double> getResiduosPorCategoriaSchool(Long schoolId) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (WasteCategory category : WasteCategory.values()) {
            Double kg = wasteEntryRepository.sumKgBySchoolAndCategory(schoolId, category);
            result.put(category.name(), kg != null ? kg : 0.0);
        }
        return result;
    }
    private Map<String, Double> getResiduosPorCategoriaClassroom(Long classroomId) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (WasteCategory category : WasteCategory.values()) {
            Double kg = wasteEntryRepository.sumKgByClassroomAndCategory(classroomId, category);
            result.put(category.name(), kg != null ? kg : 0.0);
        }
        return result;
    }
    private Double calculateAguaAhorradaSchool(Long schoolId) {
        Double total = 0.0;
        for (WasteCategory category : WasteCategory.values()) {
            Double kg = wasteEntryRepository.sumKgBySchoolAndCategory(schoolId, category);
            if (kg != null && kg > 0) {
                total += impactService.calculateWaterSaved(category.name(), kg);
            }
        }
        return total;
    }
    private Double calculateAguaAhorradaClassroom(Long classroomId) {
        Double total = 0.0;
        for (WasteCategory category : WasteCategory.values()) {
            Double kg = wasteEntryRepository.sumKgByClassroomAndCategory(classroomId, category);
            if (kg != null && kg > 0) {
                total += impactService.calculateWaterSaved(category.name(), kg);
            }
        }
        return total;
    }
}
