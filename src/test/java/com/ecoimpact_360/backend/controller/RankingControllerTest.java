package com.ecoimpact_360.backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.ecoimpact_360.backend.dto.RankingDTO;
import com.ecoimpact_360.backend.service.RankingService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RankingService rankingService;

    @Test
    void getRanking_Returns200WithList() throws Exception {
        RankingDTO ranking = RankingDTO.builder()
                .classroomName("Aula 1A")
                .totalCo2(15.0)
                .totalWaterSaved(30.0)
                .totalEntries(4L)
                .build();
        when(rankingService.getClassroomRanking()).thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/v1/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].classroomName").value("Aula 1A"))
                .andExpect(jsonPath("$[0].totalCo2").value(15.0));
    }

    @Test
    void getRanking_Returns200WithEmptyList_WhenNoData() throws Exception {
        when(rankingService.getClassroomRanking()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
