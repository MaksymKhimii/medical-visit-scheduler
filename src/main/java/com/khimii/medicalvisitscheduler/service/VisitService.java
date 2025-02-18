package com.khimii.medicalvisitscheduler.service;

import com.khimii.medicalvisitscheduler.model.Doctor;
import com.khimii.medicalvisitscheduler.model.Patient;
import com.khimii.medicalvisitscheduler.model.Visit;
import com.khimii.medicalvisitscheduler.model.dto.VisitRequest;
import com.khimii.medicalvisitscheduler.repository.DoctorRepository;
import com.khimii.medicalvisitscheduler.repository.PatientRepository;
import com.khimii.medicalvisitscheduler.repository.VisitRepository;
import com.khimii.medicalvisitscheduler.util.DateUtil;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    /**
     * Creates a new visit for a patient and doctor.
     *
     * @param request The visit request containing patient ID, doctor ID, start and end time.
     */
    @Transactional
    public void createVisit(VisitRequest request) {
        Doctor doctor = findDoctorById(request.getDoctorId());
        Patient patient = findPatientById(request.getPatientId());

        LocalDateTime start = DateUtil.convertToUtcTime(request.getStart(), doctor.getTimezone());
        LocalDateTime end = DateUtil.convertToUtcTime(request.getEnd(), doctor.getTimezone());

        checkForConflictingVisit(doctor.getId(), start, end);

        Visit visit = buildVisit(doctor, patient, start, end);

        try {
            visitRepository.save(visit);
        } catch (OptimisticLockException e) {
            log.error("Concurrent modification detected: {}", e.getMessage());
            throw new RuntimeException("Concurrent modification detected. Please try again.");
        }
    }

    /**
     * Retrieves a doctor by their ID.
     *
     * @param doctorId The ID of the doctor.
     * @return The doctor entity.
     * @throws RuntimeException if the doctor is not found.
     */
    private Doctor findDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor with ID " + doctorId + " not found"));
    }

    /**
     * Retrieves a patient by their ID.
     *
     * @param patientId The ID of the patient.
     * @return The patient entity.
     * @throws RuntimeException if the patient is not found.
     */
    private Patient findPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient with ID " + patientId + " not found"));
    }

    /**
     * Checks if there is a conflicting visit for the doctor within the specified time range.
     *
     * @param doctorId The ID of the doctor.
     * @param start    The start time of the new visit.
     * @param end      The end time of the new visit.
     * @throws RuntimeException if a conflicting visit is found.
     */
    private void checkForConflictingVisit(Long doctorId, LocalDateTime start, LocalDateTime end) {
        if (visitRepository.existsConflictingVisit(doctorId, start, end)) {
            log.warn("Conflict found for Doctor ID {} at time: {} - {}", doctorId, start, end);
            throw new RuntimeException("Doctor is not available at this time");
        }
    }

    private Visit buildVisit(Doctor doctor, Patient patient, LocalDateTime start, LocalDateTime end) {
        return Visit.builder()
                .doctor(doctor)
                .patient(patient)
                .startDateTime(start)
                .endDateTime(end)
                .build();
    }
}
