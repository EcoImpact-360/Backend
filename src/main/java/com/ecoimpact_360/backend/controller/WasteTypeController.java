package com.ecoimpact_360.backend.controller;
import com.ecoimpact_360.backend.model.WasteType;
import com.ecoimpact_360.backend.repository.WasteTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/waste-types")
@RequiredArgsConstructor
public class WasteTypeController {
    private final WasteTypeRepository wasteTypeRepository;
    @GetMapping
    public ResponseEntity<List<WasteType>> getAllWasteTypes() {
        return ResponseEntity.ok(wasteTypeRepository.findAll());
    }
}
