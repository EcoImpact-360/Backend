package com.ecoimpact_360.backend.controller;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.ecoimpact_360.backend.dto.LoginResponse;
import com.ecoimpact_360.backend.exception.UnauthorizedException;
import com.ecoimpact_360.backend.service.SchoolService;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SchoolService schoolService;
    @Test
    void login_Returns200WithToken_WhenCredentialsValid() throws Exception {
        when(schoolService.login(any())).thenReturn(
                LoginResponse.builder().token("a-token").schoolId(1L).schoolName("Colegio Central").build()
        );
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Colegio Central\",\"password\":\"secreta123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("a-token"))
                .andExpect(jsonPath("$.schoolId").value(1))
                .andExpect(jsonPath("$.schoolName").value("Colegio Central"));
    }
    @Test
    void login_Returns401_WhenCredentialsInvalid() throws Exception {
        when(schoolService.login(any())).thenThrow(new UnauthorizedException("Colegio o contraseña incorrectos"));
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Colegio Central\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
    }
    @Test
    void login_Returns400_WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"password\":\"secreta123\"}"))
                .andExpect(status().isBadRequest());
        verify(schoolService, never()).login(any());
    }
    @Test
    void login_DoesNotRequireToken() throws Exception {
        when(schoolService.login(any())).thenReturn(
                LoginResponse.builder().token("a-token").schoolId(1L).schoolName("Colegio Central").build()
        );
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Colegio Central\",\"password\":\"secreta123\"}"))
                .andExpect(status().isOk());
    }
}
