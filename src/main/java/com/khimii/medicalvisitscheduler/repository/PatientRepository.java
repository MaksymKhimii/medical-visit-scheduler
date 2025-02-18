package com.khimii.medicalvisitscheduler.repository;

import com.khimii.medicalvisitscheduler.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing patients.
 * @see Patient
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds patients who have completed visits.
     *
     * @param search    Optional search query to filter patients by first name.
     * @param doctorIds Optional list of doctor IDs to filter visits by specific doctors.
     * @param pageable  Pagination information.
     * @return A page of patients with completed visits.
     */
    @Query("""
        SELECT DISTINCT p FROM Patient p
        LEFT JOIN FETCH p.visits v
        LEFT JOIN FETCH v.doctor d
        WHERE v.endDateTime < CURRENT_TIMESTAMP
          AND (:doctorIds IS NULL OR v.doctor.id IN :doctorIds)
          AND (:search IS NULL OR p.firstName LIKE %:search%)
    """)
    Page<Patient> findPatientsWithVisits(@Param("search") String search,
                                         @Param("doctorIds") List<Long> doctorIds,
                                         Pageable pageable);
}
