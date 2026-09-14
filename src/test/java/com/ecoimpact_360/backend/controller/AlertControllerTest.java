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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecoimpact_360.backend.dto.AlertCreateRequest;
import com.ecoimpact_360.backend.dto.AlertResponseDTO;
import com.ecoimpact_360.backend.dto.AlertUpdateRequest;
import com.ecoimpact_360.backend.exception.ForbiddenException;
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
    @Autowired
    private ObjectMapper objectMapper;
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
    @Test
    void createAlert_Returns201_WithCreatedAlert() throws Exception {
        AlertCreateRequest req = new AlertCreateRequest();
        req.setClassroomId(2L);
        req.setTitle("Aviso manual");
        req.setMessage("Revisar el aula");
        when(alertService.createManualAlert(any(), eq(1L))).thenReturn(sampleAlert());
        mockMvc.perform(post("/api/v1/alerts")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
    @Test
    void createAlert_Returns400_WhenTitleMissing() throws Exception {
        AlertCreateRequest req = new AlertCreateRequest();
        req.setClassroomId(2L);
        mockMvc.perform(post("/api/v1/alerts")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void createAlert_Returns403_WhenClassroomBelongsToAnotherSchool() throws Exception {
        AlertCreateRequest req = new AlertCreateRequest();
        req.setClassroomId(99L);
        req.setTitle("Aviso manual");
        when(alertService.createManualAlert(any(), eq(1L)))
                .thenThrow(new ForbiddenException("Esta aula no pertenece a tu colegio"));
        mockMvc.perform(post("/api/v1/alerts")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
    @Test
    void updateAlert_Returns200_WithUpdatedAlert() throws Exception {
        AlertUpdateRequest req = new AlertUpdateRequest();
        req.setTitle("Titulo actualizado");
        when(alertService.updateAlert(eq(1L), any(), eq(1L))).thenReturn(sampleAlert());
        mockMvc.perform(put("/api/v1/alerts/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
    @Test
    void updateAlert_Returns404_WhenNotFound() throws Exception {
        AlertUpdateRequest req = new AlertUpdateRequest();
        req.setTitle("Titulo actualizado");
        when(alertService.updateAlert(eq(99L), any(), eq(1L)))
                .thenThrow(new ResourceNotFoundException("Alert", "id", 99L));
        mockMvc.perform(put("/api/v1/alerts/99")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteAlert_Returns204_WhenFound() throws Exception {
        mockMvc.perform(delete("/api/v1/alerts/1").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNoContent());
        verify(alertService).deleteAlert(1L, 1L);
    }
    @Test
    void deleteAlert_Returns403_WhenAlertBelongsToAnotherSchool() throws Exception {
        doThrow(new ForbiddenException("Esta alerta no pertenece a tu colegio"))
                .when(alertService).deleteAlert(1L, 1L);
        mockMvc.perform(delete("/api/v1/alerts/1").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isForbidden());
    }
}
