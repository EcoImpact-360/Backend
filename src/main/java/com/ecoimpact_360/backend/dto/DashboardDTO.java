package com.ecoimpact_360.backend.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private Double totalKgRecolectados;
    private Double totalCo2Equivalente;
    private Double totalAguaAhorrada;
    private Double arbolesEquivalentes;
    private Double kmCarroEquivalente;
    private Long totalAlertasActivas;
    private List<ClassroomRankingDTO> rankingAulas;
    private Map<String, Double> residuosPorCategoria;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassroomRankingDTO {
        private Long id;
        private String name;
        private Integer score;
    }
}
