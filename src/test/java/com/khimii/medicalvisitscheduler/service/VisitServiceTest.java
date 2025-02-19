package com.khimii.medicalvisitscheduler.service;

import com.khimii.medicalvisitscheduler.model.Doctor;
import com.khimii.medicalvisitscheduler.model.Patient;
import com.khimii.medicalvisitscheduler.model.Visit;
import com.khimii.medicalvisitscheduler.model.dto.VisitRequest;
import com.khimii.medicalvisitscheduler.repository.DoctorRepository;
import com.khimii.medicalvisitscheduler.repository.PatientRepository;
import com.khimii.medicalvisitscheduler.repository.VisitRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @InjectMocks
    private VisitService visitService;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Test
    void shouldCreateVisitSuccessfully() {
        Long doctorId = 1L;
        Long patientId = 1L;
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 11, 0);

        Doctor doctor = new Doctor(doctorId, "John", "Doe", "UTC", 5);
        Patient patient = new Patient(patientId, "Alice", "Smith");

        VisitRequest request = new VisitRequest();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setStart(start);
        request.setEnd(end);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(visitRepository.existsConflictingVisit(doctorId, start, end)).thenReturn(false);

        visitService.createVisit(request);

        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void shouldThrowExceptionWhenDoctorNotFound() {
        Long doctorId = 1L;
        Long patientId = 1L;
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 11, 0);

        VisitRequest request = new VisitRequest();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setStart(start);
        request.setEnd(end);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.createVisit(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor with ID " + doctorId + " not found");
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        Long doctorId = 1L;
        Long patientId = 1L;
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 11, 0);

        Doctor doctor = new Doctor(doctorId, "John", "Doe", "UTC", 5);

        VisitRequest request = new VisitRequest();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setStart(start);
        request.setEnd(end);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.createVisit(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient with ID " + patientId + " not found");
    }

    @Test
    void shouldThrowExceptionWhenConflictingVisitExists() {
        Long doctorId = 1L;
        Long patientId = 1L;
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 11, 0);

        Doctor doctor = new Doctor(doctorId, "John", "Doe", "UTC", 5);
        Patient patient = new Patient(patientId, "Alice", "Smith");

        VisitRequest request = new VisitRequest();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setStart(start);
        request.setEnd(end);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(visitRepository.existsConflictingVisit(doctorId, start, end)).thenReturn(true);

        assertThatThrownBy(() -> visitService.createVisit(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor is not available at this time");
    }

    @Test
    void shouldHandleOptimisticLockException() {
        Long doctorId = 1L;
        Long patientId = 1L;
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 11, 0);

        Doctor doctor = new Doctor(doctorId, "John", "Doe", "UTC", 5);
        Patient patient = new Patient(patientId, "Alice", "Smith");

        VisitRequest request = new VisitRequest();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setStart(start);
        request.setEnd(end);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(visitRepository.existsConflictingVisit(doctorId, start, end)).thenReturn(false);
        when(visitRepository.save(any(Visit.class))).thenThrow(new OptimisticLockException("Concurrent modification"));

        assertThatThrownBy(() -> visitService.createVisit(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Concurrent modification detected. Please try again.");
    }
}
