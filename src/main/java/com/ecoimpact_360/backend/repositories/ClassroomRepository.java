package com.ecoimpact_360.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecoimpact_360.backend.model.Classroom;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
}