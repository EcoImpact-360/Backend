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
import com.ecoimpact_360.backend.dto.AlertCreateRequest;
import com.ecoimpact_360.backend.dto.AlertResponseDTO;
import com.ecoimpact_360.backend.dto.AlertUpdateRequest;
import com.ecoimpact_360.backend.exception.ForbiddenException;
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.model.Alert;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.School;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.model.WasteType;
import com.ecoimpact_360.backend.model.enums.AlertType;
import com.ecoimpact_360.backend.repository.AlertRepository;
import com.ecoimpact_360.backend.repository.WasteTypeRepository;
@ExtendWith(MockitoExtension.class)
class AlertServiceTest {
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private WasteTypeRepository wasteTypeRepository;
    @Mock
    private ClassRoomService classRoomService;
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
    @Test
    void createManualAlert_SavesAlert_WithClassroomAndOptionalWasteType() {
        AlertCreateRequest req = new AlertCreateRequest();
        req.setClassroomId(2L);
        req.setWasteTypeId(1L);
        req.setTitle("Contenedor lleno");
        req.setMessage("El contenedor de plastico esta desbordado");
        req.setAlertType("CUSTOM");
        req.setTotalKg(3.0);
        when(classRoomService.getOwnedClassroomOrThrow(2L, 9L)).thenReturn(classroom);
        when(wasteTypeRepository.findById(1L)).thenReturn(Optional.of(wasteType));
        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));
        AlertResponseDTO dto = alertService.createManualAlert(req, 9L);
        assertEquals("Contenedor lleno", dto.getTitle());
        assertEquals("El contenedor de plastico esta desbordado", dto.getMessage());
        assertEquals("CUSTOM", dto.getAlertType());
        assertEquals(2L, dto.getClassroomId());
        assertEquals(1L, dto.getWasteTypeId());
        assertFalse(dto.getResolved());
        verify(alertRepository).save(any(Alert.class));
    }
    @Test
    void createManualAlert_DefaultsToCustomType_WhenAlertTypeMissing() {
        AlertCreateRequest req = new AlertCreateRequest();
        req.setClassroomId(2L);
        req.setTitle("Aviso manual");
        when(classRoomService.getOwnedClassroomOrThrow(2L, 9L)).thenReturn(classroom);
        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));
        AlertResponseDTO dto = alertService.createManualAlert(req, 9L);
        assertEquals("CUSTOM", dto.getAlertType());
        assertNull(dto.getWasteTypeId());
    }
    @Test
    void createManualAlert_ThrowsForbidden_WhenClassroomBelongsToAnotherSchool() {
        AlertCreateRequest req = new AlertCreateRequest();
        req.setClassroomId(2L);
        req.setTitle("Aviso manual");
        when(classRoomService.getOwnedClassroomOrThrow(2L, 9L)).thenThrow(new ForbiddenException("Esta aula no pertenece a tu colegio"));
        assertThrows(ForbiddenException.class, () -> alertService.createManualAlert(req, 9L));
        verify(alertRepository, never()).save(any());
    }
    @Test
    void updateAlert_AppliesProvidedFields() {
        Alert alert = new Alert();
        alert.setId(7L);
        alert.setClassroom(classroom);
        alert.setTitle("Titulo viejo");
        alert.setResolved(false);
        when(alertRepository.findById(7L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));
        AlertUpdateRequest req = new AlertUpdateRequest();
        req.setTitle("Titulo nuevo");
        req.setMessage("Mensaje nuevo");
        req.setTotalKg(9.5);
        AlertResponseDTO dto = alertService.updateAlert(7L, req, 9L);
        assertEquals("Titulo nuevo", dto.getTitle());
        assertEquals("Mensaje nuevo", dto.getMessage());
        assertEquals(9.5, dto.getTotalKg());
        verify(alertRepository).save(alert);
    }
    @Test
    void updateAlert_ThrowsForbidden_WhenAlertBelongsToAnotherSchool() {
        Alert alert = new Alert();
        alert.setId(7L);
        alert.setClassroom(classroom);
        when(alertRepository.findById(7L)).thenReturn(Optional.of(alert));
        AlertUpdateRequest req = new AlertUpdateRequest();
        req.setTitle("Titulo nuevo");
        assertThrows(ForbiddenException.class, () -> alertService.updateAlert(7L, req, 123L));
        verify(alertRepository, never()).save(any());
    }
    @Test
    void updateAlert_ThrowsResourceNotFound_WhenIdDoesNotExist() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());
        AlertUpdateRequest req = new AlertUpdateRequest();
        assertThrows(ResourceNotFoundException.class, () -> alertService.updateAlert(99L, req, 9L));
    }
    @Test
    void deleteAlert_RemovesAlert_WhenOwnedBySchool() {
        Alert alert = new Alert();
        alert.setId(7L);
        alert.setClassroom(classroom);
        when(alertRepository.findById(7L)).thenReturn(Optional.of(alert));
        alertService.deleteAlert(7L, 9L);
        verify(alertRepository).delete(alert);
    }
    @Test
    void deleteAlert_ThrowsForbidden_WhenAlertBelongsToAnotherSchool() {
        Alert alert = new Alert();
        alert.setId(7L);
        alert.setClassroom(classroom);
        when(alertRepository.findById(7L)).thenReturn(Optional.of(alert));
        assertThrows(ForbiddenException.class, () -> alertService.deleteAlert(7L, 123L));
        verify(alertRepository, never()).delete(any(Alert.class));
    }
    @Test
    void deleteAlert_ThrowsResourceNotFound_WhenIdDoesNotExist() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> alertService.deleteAlert(99L, 9L));
        verify(alertRepository, never()).delete(any(Alert.class));
    }
}
