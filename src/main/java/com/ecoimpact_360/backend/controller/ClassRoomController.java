package com.ecoimpact_360.backend.controller;
import com.ecoimpact_360.backend.dto.ClassroomCreateRequest;
import com.ecoimpact_360.backend.dto.ClassroomResponseDTO;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.service.ClassRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassRoomController {
    private final ClassRoomService classroomService;
    @GetMapping
    public ResponseEntity<List<ClassroomResponseDTO>> getAll() {
        return ResponseEntity.ok(toDtoList(classroomService.getAllClassrooms()));
    }
    @GetMapping("/ranking")
    public ResponseEntity<List<ClassroomResponseDTO>> getRanking() {
        return ResponseEntity.ok(toDtoList(classroomService.getClassroomRankingByScore()));
    }
    @PostMapping
    public ResponseEntity<ClassroomResponseDTO> create(@Valid @RequestBody ClassroomCreateRequest req) {
        Classroom saved = classroomService.createClassroom(req);
        return ResponseEntity.status(201).body(toDto(saved));
    }
    private List<ClassroomResponseDTO> toDtoList(List<Classroom> classrooms) {
        return classrooms.stream().map(this::toDto).collect(Collectors.toList());
    }
    private ClassroomResponseDTO toDto(Classroom classroom) {
        return ClassroomResponseDTO.builder()
                .id(classroom.getId())
                .name(classroom.getName())
                .score(classroom.getScore())
                .schoolId(classroom.getSchool() != null ? classroom.getSchool().getId() : null)
                .schoolName(classroom.getSchool() != null ? classroom.getSchool().getName() : null)
                .build();
    }
}
