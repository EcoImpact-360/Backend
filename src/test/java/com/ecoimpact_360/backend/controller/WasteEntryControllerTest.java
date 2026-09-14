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
import com.ecoimpact_360.backend.dto.WasteEntryResponseDTO;
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.security.TokenService;
import com.ecoimpact_360.backend.service.WasteEntryService;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WasteEntryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenService tokenService;
    @MockBean
    private WasteEntryService wasteEntryService;
    private String bearer() {
        return "Bearer " + tokenService.generateToken(1L);
    }
    @Test
    void createEntry_Returns201_WhenValid() throws Exception {
        WasteEntryResponseDTO response = WasteEntryResponseDTO.builder()
                .id(1L)
                .wasteTypeName("Plastico")
                .quantityKg(5.0)
                .co2Kg(10.0)
                .build();
        when(wasteEntryService.createWasteEntry(any(), eq(1L))).thenReturn(response);
        mockMvc.perform(post("/api/v1/waste-entries")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"classroomId\":1,\"wasteTypeId\":2,\"quantityKg\":5.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wasteTypeName").value("Plastico"));
    }
    @Test
    void createEntry_Returns401_WhenNoToken() throws Exception {
        mockMvc.perform(post("/api/v1/waste-entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"classroomId\":1,\"wasteTypeId\":2,\"quantityKg\":5.0}"))
                .andExpect(status().isUnauthorized());
        verify(wasteEntryService, never()).createWasteEntry(any(), any());
    }
    @Test
    void createEntry_Returns400_WhenQuantityIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/waste-entries")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"classroomId\":1,\"wasteTypeId\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.quantityKg").exists());
        verify(wasteEntryService, never()).createWasteEntry(any(), any());
    }
    @Test
    void createEntry_Returns400_WhenQuantityIsNegative() throws Exception {
        mockMvc.perform(post("/api/v1/waste-entries")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"classroomId\":1,\"wasteTypeId\":2,\"quantityKg\":-3}"))
                .andExpect(status().isBadRequest());
        verify(wasteEntryService, never()).createWasteEntry(any(), any());
    }
    @Test
    void createEntry_Returns400_WhenClassroomIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/waste-entries")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"wasteTypeId\":2,\"quantityKg\":5.0}"))
                .andExpect(status().isBadRequest());
        verify(wasteEntryService, never()).createWasteEntry(any(), any());
    }
    @Test
    void createEntry_Returns404_WhenReferencedResourceMissing() throws Exception {
        when(wasteEntryService.createWasteEntry(any(), eq(1L)))
                .thenThrow(new ResourceNotFoundException("WasteType", "id", 2L));
        mockMvc.perform(post("/api/v1/waste-entries")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"classroomId\":1,\"wasteTypeId\":2,\"quantityKg\":5.0}"))
                .andExpect(status().isNotFound());
    }
    @Test
    void getAllEntries_Returns200WithList() throws Exception {
        WasteEntryResponseDTO response = WasteEntryResponseDTO.builder().id(1L).wasteTypeName("Papel").build();
        when(wasteEntryService.getEntriesForSchool(1L)).thenReturn(List.of(response));
        mockMvc.perform(get("/api/v1/waste-entries").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].wasteTypeName").value("Papel"));
    }
}
