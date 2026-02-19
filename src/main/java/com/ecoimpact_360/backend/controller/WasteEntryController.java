package com.ecoimpact_360.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Importa todas las anotaciones web (incluye GetMapping)
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // <--- ¡Este es muy importante!

import com.ecoimpact_360.backend.dto.WasteEntryRequestDTO;
import com.ecoimpact_360.backend.dto.WasteEntryResponseDTO;
import com.ecoimpact_360.backend.service.WasteEntryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/waste-entries")
@RequiredArgsConstructor
public class WasteEntryController {

    private final WasteEntryService wasteEntryService;

    @PostMapping
    public ResponseEntity<WasteEntryResponseDTO> createEntry(@RequestBody WasteEntryRequestDTO request) {
        WasteEntryResponseDTO response = wasteEntryService.createWasteEntry(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping // <--- Ahora ya no es un comentario
    public ResponseEntity<List<WasteEntryResponseDTO>> getAllEntries() {
        return ResponseEntity.ok(wasteEntryService.getAllEntries());
    }
}