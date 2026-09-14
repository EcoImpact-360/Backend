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
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.ecoimpact_360.backend.dto.AlertResponseDTO;
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.security.TokenService;
import com.ecoimpact_360.backend.service.AlertService;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlertControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenService tokenService;
    @MockBean
    private AlertService alertService;
    private String bearer() {
        return "Bearer " + tokenService.generateToken(1L);
    }
    private AlertResponseDTO sampleAlert() {
        return AlertResponseDTO.builder()
                .id(1L)
                .classroomId(2L)
                .classroomName("Aula 1A")
                .wasteTypeId(3L)
                .wasteTypeName("Plastico")
                .alertType("THRESHOLD_EXCEEDED")
                .totalKg(12.5)
                .resolved(false)
                .build();
    }
    @Test
    void getPendingAlerts_Returns200WithList() throws Exception {
        when(alertService.getPendingAlertsForSchool(1L)).thenReturn(List.of(sampleAlert()));
        mockMvc.perform(get("/api/v1/alerts/pending").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].classroomName").value("Aula 1A"))
                .andExpect(jsonPath("$[0].alertType").value("THRESHOLD_EXCEEDED"));
    }
    @Test
    void getAllAlerts_Returns200WithHistory() throws Exception {
        when(alertService.getAllAlertsForSchool(1L)).thenReturn(List.of(sampleAlert()));
        mockMvc.perform(get("/api/v1/alerts/history").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalKg").value(12.5));
    }
    @Test
    void resolveAlert_Returns204_WhenFound() throws Exception {
        mockMvc.perform(patch("/api/v1/alerts/1/resolve").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNoContent());
        verify(alertService).resolveAlert(1L, 1L);
    }
    @Test
    void resolveAlert_Returns404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Alert", "id", 99L))
                .when(alertService).resolveAlert(99L, 1L);
        mockMvc.perform(patch("/api/v1/alerts/99/resolve").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }
    @Test
    void getPendingAlerts_Returns401_WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/alerts/pending"))
                .andExpect(status().isUnauthorized());
    }
}
