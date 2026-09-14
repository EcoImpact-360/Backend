package com.ecoimpact_360.backend.service;
import com.ecoimpact_360.backend.dto.ClassroomCreateRequest;
import com.ecoimpact_360.backend.exception.ForbiddenException;
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
    public List<Classroom> getClassroomsForSchool(Long schoolId) {
        return classroomRepository.findBySchoolId(schoolId);
    }
    public List<Classroom> getClassroomRankingByScoreForSchool(Long schoolId) {
        return classroomRepository.findBySchoolIdOrderByScoreDesc(schoolId);
    }
    public Classroom createClassroom(ClassroomCreateRequest req, Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));
        Classroom classroom = new Classroom();
        classroom.setName(req.getName());
        if (req.getScore() != null) {
            classroom.setScore(req.getScore());
        }
        classroom.setSchool(school);
        return classroomRepository.save(classroom);
    }
    public Classroom getOwnedClassroomOrThrow(Long classroomId, Long schoolId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));
        if (classroom.getSchool() == null || !classroom.getSchool().getId().equals(schoolId)) {
            throw new ForbiddenException("Esta aula no pertenece a tu colegio");
        }
        return classroom;
    }
}
