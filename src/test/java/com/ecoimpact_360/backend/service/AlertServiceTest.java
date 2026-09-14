package com.ecoimpact_360.backend.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ecoimpact_360.backend.dto.AlertResponseDTO;
import com.ecoimpact_360.backend.exception.ForbiddenException;
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.model.Alert;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.School;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.model.WasteType;
import com.ecoimpact_360.backend.model.enums.AlertType;
import com.ecoimpact_360.backend.repository.AlertRepository;
@ExtendWith(MockitoExtension.class)
class AlertServiceTest {
    @Mock
    private AlertRepository alertRepository;
    @InjectMocks
    private AlertService alertService;
    private WasteType wasteType;
    private Classroom classroom;
    private School school;
    @BeforeEach
    void setUp() {
        wasteType = new WasteType();
        wasteType.setId(1L);
        wasteType.setName("Plastico");
        wasteType.setMaxKgPerWeek(10.0);
        school = new School();
        school.setId(9L);
        school.setName("IES EcoImpact");
        classroom = new Classroom();
        classroom.setId(2L);
        classroom.setName("Aula 1A");
        classroom.setSchool(school);
    }
    private WasteEntry entryWithKg(double kg) {
        WasteEntry entry = new WasteEntry();
        entry.setWasteType(wasteType);
        entry.setClassroom(classroom);
        entry.setQuantityKg(kg);
        return entry;
    }
    @Test
    void checkAndCreateAlert_CreatesAlert_WhenThresholdExceeded() {
        WasteEntry entry = entryWithKg(15.0);
        when(alertRepository.existsByWasteTypeIdAndClassroomIdAndResolvedFalse(1L, 2L)).thenReturn(false);
        alertService.checkAndCreateAlert(entry);
        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        Alert saved = captor.getValue();
        assertEquals(AlertType.THRESHOLD_EXCEEDED, saved.getAlertType());
        assertEquals(wasteType, saved.getWasteType());
        assertEquals(classroom, saved.getClassroom());
        assertEquals(15.0, saved.getTotalKg());
        assertFalse(saved.getResolved());
    }
    @Test
    void checkAndCreateAlert_DoesNothing_WhenUnderThreshold() {
        WasteEntry entry = entryWithKg(5.0);
        alertService.checkAndCreateAlert(entry);
        verify(alertRepository, never()).save(any());
    }
    @Test
    void checkAndCreateAlert_DoesNotDuplicate_WhenUnresolvedAlertAlreadyExists() {
        WasteEntry entry = entryWithKg(20.0);
        when(alertRepository.existsByWasteTypeIdAndClassroomIdAndResolvedFalse(1L, 2L)).thenReturn(true);
        alertService.checkAndCreateAlert(entry);
        verify(alertRepository, never()).save(any());
    }
    @Test
    void checkAndCreateAlert_CreatesAlert_ForDifferentClassroom_EvenIfAnotherClassroomAlreadyHasOne() {
        Classroom otherClassroom = new Classroom();
        otherClassroom.setId(3L);
        otherClassroom.setName("Aula 2B");
        otherClassroom.setSchool(school);
        WasteEntry entry = new WasteEntry();
        entry.setWasteType(wasteType);
        entry.setClassroom(otherClassroom);
        entry.setQuantityKg(15.0);
        when(alertRepository.existsByWasteTypeIdAndClassroomIdAndResolvedFalse(1L, 3L)).thenReturn(false);
        alertService.checkAndCreateAlert(entry);
        verify(alertRepository).save(any(Alert.class));
    }
    @Test
    void checkAndCreateAlert_DoesNothing_WhenNoMaxConfigured() {
        wasteType.setMaxKgPerWeek(null);
        WasteEntry entry = entryWithKg(999.0);
        alertService.checkAndCreateAlert(entry);
        verify(alertRepository, never()).save(any());
    }
    @Test
    void getPendingAlerts_MapsEntityFieldsToDto() {
        Alert alert = new Alert();
        alert.setId(5L);
        alert.setWasteType(wasteType);
        alert.setClassroom(classroom);
        alert.setAlertType(AlertType.THRESHOLD_EXCEEDED);
        alert.setTotalKg(12.5);
        alert.setResolved(false);
        alert.setCreatedAt(LocalDateTime.of(2026, 2, 19, 8, 0));
        when(alertRepository.findByResolvedFalseAndClassroomSchoolId(9L)).thenReturn(List.of(alert));
        List<AlertResponseDTO> result = alertService.getPendingAlertsForSchool(9L);
        assertEquals(1, result.size());
        AlertResponseDTO dto = result.get(0);
        assertEquals(5L, dto.getId());
        assertEquals(2L, dto.getClassroomId());
        assertEquals("Aula 1A", dto.getClassroomName());
        assertEquals(1L, dto.getWasteTypeId());
        assertEquals("Plastico", dto.getWasteTypeName());
        assertEquals("THRESHOLD_EXCEEDED", dto.getAlertType());
        assertEquals(12.5, dto.getTotalKg());
        assertFalse(dto.getResolved());
    }
    @Test
    void getAllAlerts_ReturnsAllMappedAlerts() {
        Alert resolved = new Alert();
        resolved.setId(1L);
        resolved.setResolved(true);
        Alert pending = new Alert();
        pending.setId(2L);
        pending.setResolved(false);
        when(alertRepository.findByClassroomSchoolId(9L)).thenReturn(Arrays.asList(resolved, pending));
        List<AlertResponseDTO> result = alertService.getAllAlertsForSchool(9L);
        assertEquals(2, result.size());
    }
    @Test
    void resolveAlert_MarksAlertAsResolved() {
        Alert alert = new Alert();
        alert.setId(7L);
        alert.setClassroom(classroom);
        alert.setResolved(false);
        when(alertRepository.findById(7L)).thenReturn(Optional.of(alert));
        alertService.resolveAlert(7L, 9L);
        assertTrue(alert.getResolved());
        verify(alertRepository).save(alert);
    }
    @Test
    void resolveAlert_ThrowsResourceNotFound_WhenIdDoesNotExist() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> alertService.resolveAlert(99L, 9L));
        verify(alertRepository, never()).save(any());
    }
    @Test
    void resolveAlert_ThrowsForbidden_WhenAlertBelongsToAnotherSchool() {
        Alert alert = new Alert();
        alert.setId(7L);
        alert.setClassroom(classroom);
        alert.setResolved(false);
        when(alertRepository.findById(7L)).thenReturn(Optional.of(alert));
        assertThrows(ForbiddenException.class, () -> alertService.resolveAlert(7L, 123L));
        verify(alertRepository, never()).save(any());
    }
}
