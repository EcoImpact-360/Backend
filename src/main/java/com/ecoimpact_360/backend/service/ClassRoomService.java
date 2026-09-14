package com.ecoimpact_360.backend.service;
import com.ecoimpact_360.backend.dto.ClassroomCreateRequest;
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.School;
import com.ecoimpact_360.backend.repository.ClassroomRepository;
import com.ecoimpact_360.backend.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ClassRoomService {
    private final ClassroomRepository classroomRepository;
    private final SchoolRepository schoolRepository;
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }
    public List<Classroom> getClassroomRankingByScore() {
        return classroomRepository.findAllByOrderByScoreDesc();
    }
    public Classroom saveClassroom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }
    public Classroom createClassroom(ClassroomCreateRequest req) {
        School school = schoolRepository.findById(req.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", req.getSchoolId()));
        Classroom classroom = new Classroom();
        classroom.setName(req.getName());
        classroom.setScore(req.getScore());
        classroom.setSchool(school);
        return classroomRepository.save(classroom);
    }
}