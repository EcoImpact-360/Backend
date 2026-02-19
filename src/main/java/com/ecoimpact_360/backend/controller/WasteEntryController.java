package com.ecoimpact_360.backend.controller;

import com.ecoimpact_360.backend.dto.WasteEntryRequestDTO;
import com.ecoimpact_360.backend.dto.WasteEntryResponseDTO;
import com.ecoimpact_360.backend.service.WasteEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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

    // @GetMapping
    // public ResponseEntity<List<WasteEntryResponseDTO>> getAllEntries() {
    //     return ResponseEntity.ok(wasteEntryService.getAllEntries());
    // }
}