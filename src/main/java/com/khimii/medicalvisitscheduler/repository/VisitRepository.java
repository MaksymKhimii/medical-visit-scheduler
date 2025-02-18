package com.khimii.medicalvisitscheduler.repository;

import com.khimii.medicalvisitscheduler.model.Visit;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for managing visits.
 * @see Visit
 */
@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    /**
     * Checks if there is a conflicting visit for a doctor within the given time range.
     *
     * @param doctorId The ID of the doctor.
     * @param start    The start time of the new visit.
     * @param end      The end time of the new visit.
     * @return true if a conflicting visit exists, otherwise false.
     */
    @Query("""
        SELECT COUNT(v) > 0 FROM Visit v 
        WHERE v.doctor.id = :doctorId 
        AND v.startDateTime < :end 
        AND v.endDateTime > :start
    """)
    boolean existsConflictingVisit(@Param("doctorId") Long doctorId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    /**
     * Finds a visit by ID using optimistic locking.
     *
     * @param id The ID of the visit.
     * @return An optional visit.
     */
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Visit> findById(Long id);
}

