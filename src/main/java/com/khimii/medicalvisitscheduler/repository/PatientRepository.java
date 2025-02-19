package com.khimii.medicalvisitscheduler.repository;

import com.khimii.medicalvisitscheduler.model.Patient;
import com.khimii.medicalvisitscheduler.model.dto.PatientVisitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing patients.
 *
 * @see Patient
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Retrieves a paginated list of patients who have completed visits.
     *
     * @param search    Optional search query to filter patients by their first name.
     *                  If null, no filtering by name is applied.
     * @param doctorIds Optional list of doctor IDs to filter visits by specific doctors.
     *                  If null, visits from all doctors are included.
     * @param pageable  Pagination information to limit the number of results returned.
     * @return A paginated list of {@link PatientVisitResponse} containing patient details
     * and their last completed visits.
     */
    @Query("""
               SELECT new com.khimii.medicalvisitscheduler.model.dto.PatientVisitResponse(
                   p.id, p.firstName, p.lastName,\s
                   v.startDateTime, v.endDateTime,\s
                   d.id, d.firstName, d.lastName,\s
                   CAST((SELECT COUNT(DISTINCT v2.patient.id) FROM Visit v2 WHERE v2.doctor.id = d.id) AS long))
                FROM Visit v
                JOIN v.patient p
                JOIN v.doctor d
                WHERE v.endDateTime < CURRENT_TIMESTAMP
                  AND v.startDateTime = (
                      SELECT MAX(v3.startDateTime) 
                      FROM Visit v3 
                      WHERE v3.patient.id = p.id
                  )
                  AND (:doctorIds IS NULL OR v.doctor.id IN :doctorIds)
                  AND (:search IS NULL OR p.firstName LIKE %:search%)
            """)
    Page<PatientVisitResponse> findPatientsWithVisits(@Param("search") String search,
                                                      @Param("doctorIds") List<Long> doctorIds,
                                                      Pageable pageable);

}
