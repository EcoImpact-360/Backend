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
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.School;
import com.ecoimpact_360.backend.security.TokenService;
import com.ecoimpact_360.backend.service.ClassRoomService;
import java.util.ArrayList;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClassRoomControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenService tokenService;
    @MockBean
    private ClassRoomService classroomService;
    private String bearer(long schoolId) {
        return "Bearer " + tokenService.generateToken(schoolId);
    }
    private String bearer() {
        return bearer(1L);
    }
    @Test
    void getAll_Returns200WithList() throws Exception {
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Aula 1A");
        when(classroomService.getClassroomsForSchool(1L)).thenReturn(List.of(classroom));
        mockMvc.perform(get("/api/v1/classrooms").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aula 1A"));
    }
    @Test
    void getAll_Returns401_WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/classrooms"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void create_Returns201_WhenValid() throws Exception {
        Classroom saved = new Classroom();
        saved.setId(1L);
        saved.setName("Aula 1A");
        when(classroomService.createClassroom(any(), eq(1L))).thenReturn(saved);
        mockMvc.perform(post("/api/v1/classrooms")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Aula 1A\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Aula 1A"));
    }
    @Test
    void create_Returns400_WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/classrooms")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
        verify(classroomService, never()).createClassroom(any(), any());
    }
    @Test
    void create_Returns404_WhenAuthenticatedSchoolDoesNotExist() throws Exception {
        when(classroomService.createClassroom(any(), eq(99L)))
                .thenThrow(new ResourceNotFoundException("School", "id", 99L));
        mockMvc.perform(post("/api/v1/classrooms")
                        .header(HttpHeaders.AUTHORIZATION, bearer(99L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Aula 1A\"}"))
                .andExpect(status().isNotFound());
    }
    @Test
    void getAll_DoesNotSerializeCircularSchoolReference() throws Exception {
        School school = new School();
        school.setId(1L);
        school.setName("IES EcoImpact");
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Aula 1A");
        classroom.setSchool(school);
        school.setClassrooms(new ArrayList<>(List.of(classroom)));
        when(classroomService.getClassroomsForSchool(1L)).thenReturn(List.of(classroom));
        mockMvc.perform(get("/api/v1/classrooms").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].schoolId").value(1))
                .andExpect(jsonPath("$[0].schoolName").value("IES EcoImpact"))
                .andExpect(jsonPath("$[0].school").doesNotExist());
    }
    @Test
    void getRanking_Returns200WithList() throws Exception {
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Aula 1A");
        classroom.setScore(100);
        when(classroomService.getClassroomRankingByScoreForSchool(1L)).thenReturn(List.of(classroom));
        mockMvc.perform(get("/api/v1/classrooms/ranking").header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").value(100));
    }
}
