package com.ecoimpact_360.backend.controller;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.ecoimpact_360.backend.exception.ConflictException;
import com.ecoimpact_360.backend.model.School;
import com.ecoimpact_360.backend.repository.SchoolRepository;
import com.ecoimpact_360.backend.security.TokenService;
import com.ecoimpact_360.backend.service.SchoolService;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SchoolControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenService tokenService;
    @MockBean
    private SchoolRepository schoolRepository;
    @MockBean
    private SchoolService schoolService;
    private String bearer() {
        return "Bearer " + tokenService.generateToken(1L);
    }
    @Test
    void getAllSchools_Returns200WithList_WhenAuthenticated() throws Exception {
        School school = new School();
        school.setId(1L);
        school.setName("Colegio Central");
        when(schoolRepository.findAll()).thenReturn(List.of(school));
        mockMvc.perform(get("/api/v1/schools").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Colegio Central"));
    }
    @Test
    void getAllSchools_Returns401_WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/schools"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getSchoolById_Returns200_WhenFound() throws Exception {
        School school = new School();
        school.setId(1L);
        school.setName("Colegio Central");
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        mockMvc.perform(get("/api/v1/schools/1").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Colegio Central"));
    }
    @Test
    void getSchoolById_Returns404_WhenNotFound() throws Exception {
        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/schools/99").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNotFound());
    }
    @Test
    void createSchool_Returns200_WhenValid_NoTokenNeeded() throws Exception {
        School saved = new School();
        saved.setId(1L);
        saved.setName("Colegio Central");
        when(schoolService.registerSchool(any())).thenReturn(saved);
        mockMvc.perform(post("/api/v1/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Colegio Central\",\"city\":\"Madrid\",\"password\":\"secreta123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Colegio Central"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
    @Test
    void createSchool_Returns400_WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"city\":\"Madrid\",\"password\":\"secreta123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
        verify(schoolService, never()).registerSchool(any());
    }
    @Test
    void createSchool_Returns400_WhenPasswordIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Colegio Central\",\"city\":\"Madrid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
        verify(schoolService, never()).registerSchool(any());
    }
    @Test
    void createSchool_Returns400_WhenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/api/v1/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Colegio Central\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest());
        verify(schoolService, never()).registerSchool(any());
    }
    @Test
    void createSchool_Returns409_WhenNameAlreadyTaken() throws Exception {
        when(schoolService.registerSchool(any())).thenThrow(new ConflictException("Ya existe un colegio registrado con ese nombre"));
        mockMvc.perform(post("/api/v1/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Colegio Central\",\"password\":\"secreta123\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("CONFLICT"));
    }
}
