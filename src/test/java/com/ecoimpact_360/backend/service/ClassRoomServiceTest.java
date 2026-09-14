package com.ecoimpact_360.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecoimpact_360.backend.dto.ClassroomCreateRequest;
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.School;
import com.ecoimpact_360.backend.repository.ClassroomRepository;
import com.ecoimpact_360.backend.repository.SchoolRepository;

@ExtendWith(MockitoExtension.class)
class ClassRoomServiceTest {

    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private SchoolRepository schoolRepository;

    @InjectMocks
    private ClassRoomService classRoomService;

    private School school;

    @BeforeEach
    void setUp() {
        school = new School();
        school.setId(1L);
        school.setName("Colegio Central");
    }

    @Test
    void createClassroom_SavesClassroomLinkedToSchool() {
        ClassroomCreateRequest req = new ClassroomCreateRequest();
        req.setName("Aula 1A");
        req.setSchoolId(1L);
        req.setScore(10);

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(classroomRepository.save(any(Classroom.class))).thenAnswer(inv -> inv.getArgument(0));

        Classroom result = classRoomService.createClassroom(req);

        assertEquals("Aula 1A", result.getName());
        assertEquals(10, result.getScore());
        assertEquals(school, result.getSchool());
    }

    @Test
    void createClassroom_ThrowsResourceNotFound_WhenSchoolMissing() {
        ClassroomCreateRequest req = new ClassroomCreateRequest();
        req.setName("Aula 1A");
        req.setSchoolId(99L);

        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> classRoomService.createClassroom(req));
        verify(classroomRepository, never()).save(any());
    }

    @Test
    void getClassroomRankingByScore_DelegatesToRepository() {
        Classroom c1 = new Classroom();
        c1.setId(1L);
        c1.setScore(100);
        when(classroomRepository.findAllByOrderByScoreDesc()).thenReturn(List.of(c1));

        List<Classroom> result = classRoomService.getClassroomRankingByScore();

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getScore());
    }
}
