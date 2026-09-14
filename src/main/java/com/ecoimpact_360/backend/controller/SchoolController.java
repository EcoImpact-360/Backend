package com.ecoimpact_360.backend.controller;
import com.ecoimpact_360.backend.dto.SchoolResponseDTO;
import com.ecoimpact_360.backend.model.School;
import com.ecoimpact_360.backend.repository.SchoolRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
public class SchoolController {
    private final SchoolRepository schoolRepository;
    @GetMapping
    public ResponseEntity<List<SchoolResponseDTO>> getAllSchools() {
        return ResponseEntity.ok(schoolRepository.findAll().stream().map(this::toDto).collect(Collectors.toList()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<SchoolResponseDTO> getSchoolById(@PathVariable Long id) {
        return schoolRepository.findById(id)
                .map(school -> ResponseEntity.ok(toDto(school)))
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<SchoolResponseDTO> createSchool(@Valid @RequestBody School school) {
        School savedSchool = schoolRepository.save(school);
        return ResponseEntity.ok(toDto(savedSchool));
    }
    private SchoolResponseDTO toDto(School school) {
        return SchoolResponseDTO.builder()
                .id(school.getId())
                .name(school.getName())
                .city(school.getCity())
                .build();
    }
}
