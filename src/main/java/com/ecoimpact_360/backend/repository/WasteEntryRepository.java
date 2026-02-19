package com.ecoimpact_360.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.model.enums.WasteCategory;
import com.ecoimpact_360.backend.model.enums.WasteStatus;

@Repository
public interface WasteEntryRepository extends JpaRepository<WasteEntry, Long> {

    List<WasteEntry> findByClassroomId(Long classroomId);
    List<WasteEntry> findByStatus(WasteStatus status);
    List<WasteEntry> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(e.quantityKg) FROM WasteEntry e " +
           "WHERE e.wasteType.id = :typeId " +
           "AND e.createdAt BETWEEN :start AND :end")
    Double sumKgByTypeAndWeek(@Param("typeId") Long typeId,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    @Query("SELECT SUM(e.quantityKg) FROM WasteEntry e " +
           "WHERE e.classroom.id = :classroomId " +
           "AND e.createdAt BETWEEN :start AND :end")
    Double sumKgByClassroomAndWeek(@Param("classroomId") Long classroomId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(e.quantityKg) FROM WasteEntry e")
    Double sumAllKg();
    
    @Query("SELECT SUM(e.co2Equivalent) FROM WasteEntry e")
    Double sumAllCo2();
    
    @Query("SELECT SUM(e.quantityKg) FROM WasteEntry e " +
           "WHERE e.classroom.id = :classroomId")
    Double sumKgByClassroom(@Param("classroomId") Long classroomId);
    
    @Query("SELECT SUM(e.co2Equivalent) FROM WasteEntry e " +
           "WHERE e.classroom.id = :classroomId")
    Double sumCo2ByClassroom(@Param("classroomId") Long classroomId);
    
    @Query("SELECT SUM(e.quantityKg) FROM WasteEntry e " +
           "WHERE e.wasteType.category = :category")
    Double sumKgByCategory(@Param("category") WasteCategory category);
}