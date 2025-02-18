package com.khimii.medicalvisitscheduler.repository;

import com.khimii.medicalvisitscheduler.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing doctors.
 * @see Doctor
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
