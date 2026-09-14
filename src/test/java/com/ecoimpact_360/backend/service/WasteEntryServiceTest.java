package com.ecoimpact_360.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecoimpact_360.backend.dto.WasteEntryRequestDTO;
import com.ecoimpact_360.backend.dto.WasteEntryResponseDTO;
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.model.WasteType;
import com.ecoimpact_360.backend.repository.ClassroomRepository;
import com.ecoimpact_360.backend.repository.WasteEntryRepository;
import com.ecoimpact_360.backend.repository.WasteTypeRepository;

@ExtendWith(MockitoExtension.class)
class WasteEntryServiceTest {

    @Mock
    private WasteEntryRepository wasteEntryRepository;
    @Mock
    private WasteTypeRepository wasteTypeRepository;
    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private ImpactService impactService;
    @Mock
    private AlertService alertService;

    @InjectMocks
    private WasteEntryService wasteEntryService;

    private WasteType wasteType;
    private Classroom classroom;
    private WasteEntryRequestDTO request;

    @BeforeEach
    void setUp() {
        wasteType = new WasteType();
        wasteType.setId(1L);
        wasteType.setName("Plastico");

        classroom = new Classroom();
        classroom.setId(2L);
        classroom.setName("Aula 1A");

        request = new WasteEntryRequestDTO(2L, 1L, 5.0);
    }

    @Test
    void createWasteEntry_SavesEntryAndReturnsCalculatedImpact() {
        when(wasteTypeRepository.findById(1L)).thenReturn(Optional.of(wasteType));
        when(classroomRepository.findById(2L)).thenReturn(Optional.of(classroom));
        when(impactService.calculateCo2(wasteType, 5.0)).thenReturn(10.0);
        when(impactService.calculateWaterSaved(wasteType, 5.0)).thenReturn(50.0);
        when(impactService.calculateTreesEquivalent(10.0)).thenReturn(0.5);
        when(impactService.calculateKmCarEquivalent(10.0)).thenReturn(83.3);
        when(wasteEntryRepository.save(any(WasteEntry.class))).thenAnswer(invocation -> {
            WasteEntry entry = invocation.getArgument(0);
            entry.setId(99L);
            return entry;
        });

        WasteEntryResponseDTO response = wasteEntryService.createWasteEntry(request);

        assertEquals(99L, response.getId());
        assertEquals("Plastico", response.getWasteTypeName());
        assertEquals(5.0, response.getQuantityKg());
        assertEquals(10.0, response.getCo2Kg());
        assertEquals(50.0, response.getWaterSaved());

        verify(alertService).checkAndCreateAlert(any(WasteEntry.class));
    }

    @Test
    void createWasteEntry_ThrowsResourceNotFound_WhenWasteTypeMissing() {
        when(wasteTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> wasteEntryService.createWasteEntry(request));
        verify(wasteEntryRepository, never()).save(any());
        verify(alertService, never()).checkAndCreateAlert(any());
    }

    @Test
    void createWasteEntry_ThrowsResourceNotFound_WhenClassroomMissing() {
        when(wasteTypeRepository.findById(1L)).thenReturn(Optional.of(wasteType));
        when(classroomRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> wasteEntryService.createWasteEntry(request));
        verify(wasteEntryRepository, never()).save(any());
    }

    @Test
    void getAllEntries_MapsRepositoryResultsToDto() {
        WasteEntry entry = new WasteEntry();
        entry.setId(1L);
        entry.setWasteType(wasteType);
        entry.setQuantityKg(3.0);
        entry.setCo2Equivalent(6.0);

        when(wasteEntryRepository.findAll()).thenReturn(List.of(entry));
        when(impactService.calculateWaterSaved(wasteType, 3.0)).thenReturn(30.0);
        when(impactService.calculateTreesEquivalent(6.0)).thenReturn(0.3);
        when(impactService.calculateKmCarEquivalent(6.0)).thenReturn(50.0);

        List<WasteEntryResponseDTO> result = wasteEntryService.getAllEntries();

        assertEquals(1, result.size());
        assertEquals("Plastico", result.get(0).getWasteTypeName());
        assertEquals(30.0, result.get(0).getWaterSaved());
    }
}
