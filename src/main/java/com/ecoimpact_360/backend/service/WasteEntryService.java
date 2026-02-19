package com.ecoimpact_360.backend.service;

import com.ecoimpact_360.backend.dto.WasteEntryRequestDTO;
import com.ecoimpact_360.backend.dto.WasteEntryResponseDTO;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.model.WasteType;
import com.ecoimpact_360.backend.repository.ClassroomRepository;
import com.ecoimpact_360.backend.repository.WasteEntryRepository;
import com.ecoimpact_360.backend.repository.WasteTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WasteEntryService {

    private final WasteEntryRepository wasteEntryRepository;
    private final WasteTypeRepository wasteTypeRepository;
    private final ClassroomRepository classroomRepository;
    private final ImpactService impactService; 

    @Transactional
    public WasteEntryResponseDTO createWasteEntry(WasteEntryRequestDTO dto) {
        
        WasteType type = wasteTypeRepository.findById(dto.getWasteTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo de residuo no encontrado"));
        
        Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        
        double co2 = impactService.calculateCo2(type, dto.getQuantityKg());
        double water = impactService.calculateWaterSaved(type, dto.getQuantityKg());

       
        WasteEntry entry = new WasteEntry();
        entry.setWasteType(type);
        entry.setClassroom(classroom);
        entry.setQuantityKg(dto.getQuantityKg());
        entry.setCo2Equivalent(co2);
        entry.setCreatedAt(LocalDateTime.now());
       

        WasteEntry savedEntry = wasteEntryRepository.save(entry);

       
        return WasteEntryResponseDTO.builder()
                .id(savedEntry.getId())
                .wasteTypeName(type.getName())
                .quantityKg(savedEntry.getQuantityKg())
                .co2Kg(co2)
                .waterSaved(water)
                .treesEquivalent(impactService.calculateTreesEquivalent(co2))
                .kmCarEquivalent(impactService.calculateKmCarEquivalent(co2))
                .build();
    }

    //añadi getAllEntries
        @Transactional(readOnly = true)
    public List<WasteEntryResponseDTO> getAllEntries() {
        return wasteEntryRepository.findAll().stream()
                .map(entry -> {
                   
                    double water = impactService.calculateWaterSaved(entry.getWasteType(), entry.getQuantityKg());
                    
                    return WasteEntryResponseDTO.builder()
                            .id(entry.getId())
                            .wasteTypeName(entry.getWasteType().getName())
                            .quantityKg(entry.getQuantityKg())
                            .co2Kg(entry.getCo2Equivalent())
                            .waterSaved(water)
                            .treesEquivalent(impactService.calculateTreesEquivalent(entry.getCo2Equivalent()))
                            .kmCarEquivalent(impactService.calculateKmCarEquivalent(entry.getCo2Equivalent()))
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    

}