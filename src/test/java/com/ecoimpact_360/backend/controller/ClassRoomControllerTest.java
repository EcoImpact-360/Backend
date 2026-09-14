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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.service.ClassRoomService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClassRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassRoomService classroomService;

    @Test
    void getAll_Returns200WithList() throws Exception {
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Aula 1A");
        when(classroomService.getAllClassrooms()).thenReturn(List.of(classroom));

        mockMvc.perform(get("/api/v1/classrooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aula 1A"));
    }

    @Test
    void create_Returns201_WhenValid() throws Exception {
        Classroom saved = new Classroom();
        saved.setId(1L);
        saved.setName("Aula 1A");
        when(classroomService.createClassroom(any())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/classrooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Aula 1A\",\"schoolId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Aula 1A"));
    }

    @Test
    void create_Returns400_WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/classrooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"schoolId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));

        verify(classroomService, never()).createClassroom(any());
    }

    @Test
    void create_Returns400_WhenSchoolIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/classrooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Aula 1A\"}"))
                .andExpect(status().isBadRequest());

        verify(classroomService, never()).createClassroom(any());
    }

    @Test
    void create_Returns404_WhenSchoolDoesNotExist() throws Exception {
        when(classroomService.createClassroom(any()))
                .thenThrow(new ResourceNotFoundException("School", "id", 99L));

        mockMvc.perform(post("/api/v1/classrooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Aula 1A\",\"schoolId\":99}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRanking_Returns200WithList() throws Exception {
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Aula 1A");
        classroom.setScore(100);
        when(classroomService.getClassroomRankingByScore()).thenReturn(List.of(classroom));

        mockMvc.perform(get("/api/v1/classrooms/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").value(100));
    }
}
