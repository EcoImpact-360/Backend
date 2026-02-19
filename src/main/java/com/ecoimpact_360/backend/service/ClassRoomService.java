package com.ecoimpact_360.backend.service;

import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassRoomService {

    private final ClassroomRepository classroomRepository;

    // Listar todas para el desplegable del formulario
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    // El corazón del T5: Ranking por puntuación (Score)
    public List<Classroom> getClassroomRankingByScore() {
        return classroomRepository.findAllByOrderByScoreDesc();
    }
}