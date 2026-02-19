package com.ecoimpact_360.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecoimpact_360.backend.model.School;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
}