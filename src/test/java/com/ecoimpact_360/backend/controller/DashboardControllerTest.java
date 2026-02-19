package com.ecoimpact_360.backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.ecoimpact_360.backend.dto.DashboardDTO;
import com.ecoimpact_360.backend.service.DashboardService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void getGlobalDashboard_Returns200() throws Exception {
        DashboardDTO dashboard = createMockDashboard();
        when(dashboardService.getGlobalStats()).thenReturn(dashboard);

        mockMvc.perform(get("/api/dashboard/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalKgRecolectados").value(100.0))
                .andExpect(jsonPath("$.totalCo2Equivalente").value(150.0))
                .andExpect(jsonPath("$.totalAguaAhorrada").value(500.0))
                .andExpect(jsonPath("$.arbolesEquivalentes").value(7.5))
                .andExpect(jsonPath("$.kmCarroEquivalente").value(1250.0))
                .andExpect(jsonPath("$.totalAlertasActivas").value(3))
                .andExpect(jsonPath("$.rankingAulas").isArray())
                .andExpect(jsonPath("$.rankingAulas").isNotEmpty())
                .andExpect(jsonPath("$.residuosPorCategoria").isMap());
    }

    @Test
    void getGlobalDashboard_ReturnsEmptyMetrics_WhenNoData() throws Exception {
        DashboardDTO emptyDashboard = DashboardDTO.builder()
                .totalKgRecolectados(0.0)
                .totalCo2Equivalente(0.0)
                .totalAguaAhorrada(0.0)
                .arbolesEquivalentes(0.0)
                .kmCarroEquivalente(0.0)
                .totalAlertasActivas(0L)
                .rankingAulas(Collections.emptyList())
                .residuosPorCategoria(new HashMap<>())
                .build();
        when(dashboardService.getGlobalStats()).thenReturn(emptyDashboard);

        mockMvc.perform(get("/api/dashboard/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalKgRecolectados").value(0.0))
                .andExpect(jsonPath("$.totalAlertasActivas").value(0))
                .andExpect(jsonPath("$.rankingAulas").isEmpty());
    }

    @Test
    void getClassroomDashboard_Returns200() throws Exception {
        DashboardDTO dashboard = createMockDashboard();
        when(dashboardService.getClassroomStats(1L)).thenReturn(dashboard);

        mockMvc.perform(get("/api/dashboard/classroom/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalKgRecolectados").value(100.0))
                .andExpect(jsonPath("$.totalCo2Equivalente").value(150.0))
                .andExpect(jsonPath("$.rankingAulas").isArray());
    }

    @Test
    void getClassroomDashboard_WithInvalidId_ReturnsData() throws Exception {
        DashboardDTO dashboard = createMockDashboard();
        when(dashboardService.getClassroomStats(999L)).thenReturn(dashboard);

        mockMvc.perform(get("/api/dashboard/classroom/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalKgRecolectados").value(100.0));
    }

    @Test
    void getGlobalDashboard_ContainsCorrectRankingAulas() throws Exception {
        DashboardDTO dashboard = DashboardDTO.builder()
                .totalKgRecolectados(100.0)
                .totalCo2Equivalente(150.0)
                .totalAguaAhorrada(500.0)
                .arbolesEquivalentes(7.5)
                .kmCarroEquivalente(1250.0)
                .totalAlertasActivas(2L)
                .rankingAulas(Arrays.asList(
                        DashboardDTO.ClassroomRankingDTO.builder().id(1L).name("Aula 1A").score(100).build(),
                        DashboardDTO.ClassroomRankingDTO.builder().id(2L).name("Aula 2B").score(50).build()
                ))
                .residuosPorCategoria(new HashMap<>())
                .build();
        when(dashboardService.getGlobalStats()).thenReturn(dashboard);

        mockMvc.perform(get("/api/dashboard/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rankingAulas[0].id").value(1))
                .andExpect(jsonPath("$.rankingAulas[0].name").value("Aula 1A"))
                .andExpect(jsonPath("$.rankingAulas[0].score").value(100))
                .andExpect(jsonPath("$.rankingAulas[1].id").value(2))
                .andExpect(jsonPath("$.rankingAulas[1].name").value("Aula 2B"))
                .andExpect(jsonPath("$.rankingAulas[1].score").value(50));
    }

    @Test
    void getGlobalDashboard_ContainsResiduosPorCategoria() throws Exception {
        DashboardDTO dashboard = DashboardDTO.builder()
                .totalKgRecolectados(100.0)
                .totalCo2Equivalente(150.0)
                .totalAguaAhorrada(500.0)
                .arbolesEquivalentes(7.5)
                .kmCarroEquivalente(1250.0)
                .totalAlertasActivas(1L)
                .rankingAulas(Collections.emptyList())
                .residuosPorCategoria(createResiduosPorCategoria())
                .build();
        when(dashboardService.getGlobalStats()).thenReturn(dashboard);

        mockMvc.perform(get("/api/dashboard/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.residuosPorCategoria.PLASTIC").value(50.0))
                .andExpect(jsonPath("$.residuosPorCategoria.PAPEL").value(30.0))
                .andExpect(jsonPath("$.residuosPorCategoria.VIDRIO").value(20.0));
    }

    @Test
    void getClassroomDashboard_WithZeroId_Returns200() throws Exception {
        DashboardDTO dashboard = createMockDashboard();
        when(dashboardService.getClassroomStats(0L)).thenReturn(dashboard);

        mockMvc.perform(get("/api/dashboard/classroom/0"))
                .andExpect(status().isOk());
    }

    private DashboardDTO createMockDashboard() {
        return DashboardDTO.builder()
                .totalKgRecolectados(100.0)
                .totalCo2Equivalente(150.0)
                .totalAguaAhorrada(500.0)
                .arbolesEquivalentes(7.5)
                .kmCarroEquivalente(1250.0)
                .totalAlertasActivas(3L)
                .rankingAulas(Arrays.asList(
                        DashboardDTO.ClassroomRankingDTO.builder().id(1L).name("Aula 1A").score(100).build()
                ))
                .residuosPorCategoria(createResiduosPorCategoria())
                .build();
    }

    private Map<String, Double> createResiduosPorCategoria() {
        Map<String, Double> map = new HashMap<>();
        map.put("PLASTIC", 50.0);
        map.put("PAPEL", 30.0);
        map.put("VIDRIO", 20.0);
        map.put("ORGANIC", 0.0);
        map.put("GENERAL", 0.0);
        return map;
    }
}
