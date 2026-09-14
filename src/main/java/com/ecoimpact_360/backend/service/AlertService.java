package com.ecoimpact_360.backend.service;
import com.ecoimpact_360.backend.dto.AlertResponseDTO;
import com.ecoimpact_360.backend.exception.ForbiddenException;
import com.ecoimpact_360.backend.exception.ResourceNotFoundException;
import com.ecoimpact_360.backend.model.Alert;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.model.enums.AlertType;
import com.ecoimpact_360.backend.repository.AlertRepository;
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
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
        if (alert.getClassroom() == null || alert.getClassroom().getSchool() == null
                || !alert.getClassroom().getSchool().getId().equals(schoolId)) {
            throw new ForbiddenException("Esta alerta no pertenece a tu colegio");
        }
        alert.setResolved(true);
        alertRepository.save(alert);
    }
    private AlertResponseDTO toDto(Alert alert) {
        return AlertResponseDTO.builder()
                .id(alert.getId())
                .classroomId(alert.getClassroom() != null ? alert.getClassroom().getId() : null)
                .classroomName(alert.getClassroom() != null ? alert.getClassroom().getName() : null)
                .wasteTypeId(alert.getWasteType() != null ? alert.getWasteType().getId() : null)
                .wasteTypeName(alert.getWasteType() != null ? alert.getWasteType().getName() : null)
                .alertType(alert.getAlertType() != null ? alert.getAlertType().name() : null)
                .totalKg(alert.getTotalKg())
                .resolved(alert.getResolved())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
