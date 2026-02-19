package com.ecoimpact_360.backend.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecoimpact_360.backend.dto.DashboardDTO;
import com.ecoimpact_360.backend.dto.DashboardDTO.ClassroomRankingDTO;
import com.ecoimpact_360.backend.model.Classroom;
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
    private final ImpactService impactService;

    public DashboardDTO getGlobalStats() {
        Double totalKg = wasteEntryRepository.sumAllKg() != null ? wasteEntryRepository.sumAllKg() : 0.0;
        Double totalCo2 = wasteEntryRepository.sumAllCo2() != null ? wasteEntryRepository.sumAllCo2() : 0.0;
        
        Double aguaAhorrada = calculateAguaAhorradaGlobal();
        Double arbresEquivalentes = impactService.calculateTreesEquivalent(totalCo2);
        Double kmCarroEquivalente = impactService.calculateKmCarEquivalent(totalCo2);
        long alertasActivas = alertRepository.countByResolvedFalse();
        
        List<ClassroomRankingDTO> ranking = getRankingAulas();
        Map<String, Double> residuosPorCategoria = getResiduosPorCategoriaGlobal();

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

    public DashboardDTO getClassroomStats(Long classroomId) {
        Double totalKg = wasteEntryRepository.sumKgByClassroom(classroomId) != null 
                ? wasteEntryRepository.sumKgByClassroom(classroomId) : 0.0;
        Double totalCo2 = wasteEntryRepository.sumCo2ByClassroom(classroomId) != null 
                ? wasteEntryRepository.sumCo2ByClassroom(classroomId) : 0.0;
        
        Double aguaAhorrada = calculateAguaAhorradaClassroom(classroomId);
        Double arbresEquivalentes = impactService.calculateTreesEquivalent(totalCo2);
        Double kmCarroEquivalente = impactService.calculateKmCarEquivalent(totalCo2);
        
        Classroom classroom = classroomRepository.findById(classroomId).orElse(null);
        List<ClassroomRankingDTO> ranking = getRankingAulas();
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

    private List<ClassroomRankingDTO> getRankingAulas() {
        return classroomRepository.findAllByOrderByScoreDesc().stream()
                .map(c -> ClassroomRankingDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .score(c.getScore())
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, Double> getResiduosPorCategoriaGlobal() {
        Map<String, Double> result = new LinkedHashMap<>();
        for (WasteCategory category : WasteCategory.values()) {
            Double kg = wasteEntryRepository.sumKgByCategory(category);
            result.put(category.name(), kg != null ? kg : 0.0);
        }
        return result;
    }

    private Map<String, Double> getResiduosPorCategoriaClassroom(Long classroomId) {
        Map<String, Double> result = new LinkedHashMap<>();
        List<Classroom> classroom = classroomRepository.findAll().stream()
                .filter(c -> c.getId().equals(classroomId))
                .collect(Collectors.toList());
        
        for (WasteCategory category : WasteCategory.values()) {
            result.put(category.name(), 0.0);
        }
        return result;
    }

    private Double calculateAguaAhorradaGlobal() {
        Double total = 0.0;
        for (WasteCategory category : WasteCategory.values()) {
            Double kg = wasteEntryRepository.sumKgByCategory(category);
            if (kg != null && kg > 0) {
                total += impactService.calculateWaterSaved(category.name(), kg);
            }
        }
        return total;
    }

    private Double calculateAguaAhorradaClassroom(Long classroomId) {
        Double totalKg = wasteEntryRepository.sumKgByClassroom(classroomId);
        if (totalKg == null || totalKg == 0) {
            return 0.0;
        }
        return impactService.calculateWaterSaved("GENERAL", totalKg);
    }
}
