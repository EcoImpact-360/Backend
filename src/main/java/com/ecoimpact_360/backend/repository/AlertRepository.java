package com.ecoimpact_360.backend.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ecoimpact_360.backend.model.Alert;
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    boolean existsByWasteTypeIdAndClassroomIdAndResolvedFalse(Long wasteTypeId, Long classroomId);
    List<Alert> findByResolvedFalseAndClassroomSchoolId(Long schoolId);
    List<Alert> findByClassroomSchoolId(Long schoolId);
    long countByResolvedFalseAndClassroomSchoolId(Long schoolId);
}
