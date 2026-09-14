package com.ecoimpact_360.backend.service;
import com.ecoimpact_360.backend.dto.AlertCreateRequest;
import com.ecoimpact_360.backend.dto.AlertResponseDTO;
import com.ecoimpact_360.backend.dto.AlertUpdateRequest;
import com.ecoimpact_360.backend.exception.ForbiddenException;
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.model.Alert;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.model.WasteType;
import com.ecoimpact_360.backend.model.enums.AlertType;
import com.ecoimpact_360.backend.repository.AlertRepository;
import com.ecoimpact_360.backend.repository.WasteTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final WasteTypeRepository wasteTypeRepository;
    private final ClassRoomService classRoomService;
    @Transactional
    public void checkAndCreateAlert(WasteEntry entry) {
        Double maxAllowed = entry.getWasteType().getMaxKgPerWeek();
        if (maxAllowed != null && entry.getQuantityKg() > maxAllowed) {
            boolean alreadyHasAlert = alertRepository
                .existsByWasteTypeIdAndClassroomIdAndResolvedFalse(entry.getWasteType().getId(), entry.getClassroom().getId());
            if (!alreadyHasAlert) {
                Alert alert = new Alert();
                alert.setWasteType(entry.getWasteType());
                alert.setClassroom(entry.getClassroom());
                alert.setAlertType(AlertType.THRESHOLD_EXCEEDED);
                alert.setTotalKg(entry.getQuantityKg());
                alert.setResolved(false);
                alert.setCreatedAt(LocalDateTime.now());
                alertRepository.save(alert);
            }
        }
    }
    @Transactional
    public AlertResponseDTO createManualAlert(AlertCreateRequest req, Long schoolId) {
        Classroom classroom = classRoomService.getOwnedClassroomOrThrow(req.getClassroomId(), schoolId);
        WasteType wasteType = null;
        if (req.getWasteTypeId() != null) {
            wasteType = wasteTypeRepository.findById(req.getWasteTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("WasteType", "id", req.getWasteTypeId()));
        }
        Alert alert = new Alert();
        alert.setClassroom(classroom);
        alert.setWasteType(wasteType);
        alert.setAlertType(parseAlertType(req.getAlertType()));
        alert.setTitle(req.getTitle());
        alert.setMessage(req.getMessage());
        alert.setTotalKg(req.getTotalKg());
        alert.setResolved(false);
        alert.setCreatedAt(LocalDateTime.now());
        return toDto(alertRepository.save(alert));
    }
    @Transactional
    public AlertResponseDTO updateAlert(Long id, AlertUpdateRequest req, Long schoolId) {
        Alert alert = getOwnedAlertOrThrow(id, schoolId);
        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            alert.setTitle(req.getTitle());
        }
        if (req.getMessage() != null) {
            alert.setMessage(req.getMessage());
        }
        if (req.getAlertType() != null && !req.getAlertType().isBlank()) {
            alert.setAlertType(parseAlertType(req.getAlertType()));
        }
        if (req.getTotalKg() != null) {
            alert.setTotalKg(req.getTotalKg());
        }
        return toDto(alertRepository.save(alert));
    }
    @Transactional
    public void deleteAlert(Long id, Long schoolId) {
        Alert alert = getOwnedAlertOrThrow(id, schoolId);
        alertRepository.delete(alert);
    }
    @Transactional(readOnly = true)
    public List<AlertResponseDTO> getPendingAlertsForSchool(Long schoolId) {
        return alertRepository.findByResolvedFalseAndClassroomSchoolId(schoolId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<AlertResponseDTO> getAllAlertsForSchool(Long schoolId) {
        return alertRepository.findByClassroomSchoolId(schoolId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void resolveAlert(Long id, Long schoolId) {
        Alert alert = getOwnedAlertOrThrow(id, schoolId);
        alert.setResolved(true);
        alertRepository.save(alert);
    }
    private Alert getOwnedAlertOrThrow(Long id, Long schoolId) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
        if (alert.getClassroom() == null || alert.getClassroom().getSchool() == null
                || !alert.getClassroom().getSchool().getId().equals(schoolId)) {
            throw new ForbiddenException("Esta alerta no pertenece a tu colegio");
        }
        return alert;
    }
    private AlertType parseAlertType(String rawAlertType) {
        if (rawAlertType == null || rawAlertType.isBlank()) {
            return AlertType.CUSTOM;
        }
        try {
            return AlertType.valueOf(rawAlertType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return AlertType.CUSTOM;
        }
    }
    private AlertResponseDTO toDto(Alert alert) {
        return AlertResponseDTO.builder()
                .id(alert.getId())
                .classroomId(alert.getClassroom() != null ? alert.getClassroom().getId() : null)
                .classroomName(alert.getClassroom() != null ? alert.getClassroom().getName() : null)
                .wasteTypeId(alert.getWasteType() != null ? alert.getWasteType().getId() : null)
                .wasteTypeName(alert.getWasteType() != null ? alert.getWasteType().getName() : null)
                .alertType(alert.getAlertType() != null ? alert.getAlertType().name() : null)
                .title(alert.getTitle())
                .message(alert.getMessage())
                .totalKg(alert.getTotalKg())
                .resolved(alert.getResolved())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
