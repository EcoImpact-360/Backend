package com.ecoimpact_360.backend.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ecoimpact_360.backend.model.School;
@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
