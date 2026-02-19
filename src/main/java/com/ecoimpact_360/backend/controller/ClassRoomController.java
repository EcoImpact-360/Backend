package com.ecoimpact_360.backend.controller;

import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.service.ClassRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassRoomController {

    private final ClassRoomService classroomService;

    
    @GetMapping
    public ResponseEntity<List<Classroom>> getAll() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    
    @GetMapping("/ranking")
    public ResponseEntity<List<Classroom>> getRanking() {
        return ResponseEntity.ok(classroomService.getClassroomRankingByScore());
    }
}
