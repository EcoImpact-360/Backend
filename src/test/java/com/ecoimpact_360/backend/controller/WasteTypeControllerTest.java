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
import com.ecoimpact_360.backend.model.WasteType;
import com.ecoimpact_360.backend.model.enums.WasteCategory;
import com.ecoimpact_360.backend.repository.WasteTypeRepository;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WasteTypeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WasteTypeRepository wasteTypeRepository;
    @Test
    void getAllWasteTypes_Returns200WithList() throws Exception {
        WasteType type = new WasteType();
        type.setId(1L);
        type.setName("Plastico");
        type.setCategory(WasteCategory.PLASTIC);
        when(wasteTypeRepository.findAll()).thenReturn(List.of(type));
        mockMvc.perform(get("/api/v1/waste-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Plastico"))
                .andExpect(jsonPath("$[0].category").value("PLASTIC"));
    }
    @Test
    void getAllWasteTypes_Returns200WithEmptyList_WhenNoData() throws Exception {
        when(wasteTypeRepository.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/waste-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
