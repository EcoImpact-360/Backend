package com.ecoimpact_360.backend.controller;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecoimpact_360.backend.dto.WasteEntryRequestDTO;
import com.ecoimpact_360.backend.dto.WasteEntryResponseDTO;
import com.ecoimpact_360.backend.security.AuthInterceptor;
import com.ecoimpact_360.backend.service.WasteEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/v1/waste-entries")
@RequiredArgsConstructor
public class WasteEntryController {
    private final WasteEntryService wasteEntryService;
    @PostMapping
    public ResponseEntity<WasteEntryResponseDTO> createEntry(@Valid @RequestBody WasteEntryRequestDTO request,
                                                               @RequestAttribute(AuthInterceptor.SCHOOL_ID_ATTRIBUTE) Long schoolId) {
        WasteEntryResponseDTO response = wasteEntryService.createWasteEntry(request, schoolId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<WasteEntryResponseDTO>> getAllEntries(@RequestAttribute(AuthInterceptor.SCHOOL_ID_ATTRIBUTE) Long schoolId) {
        return ResponseEntity.ok(wasteEntryService.getEntriesForSchool(schoolId));
    }
}
