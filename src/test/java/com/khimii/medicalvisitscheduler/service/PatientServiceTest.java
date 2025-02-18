package com.khimii.medicalvisitscheduler.service;

import com.khimii.medicalvisitscheduler.model.Doctor;
import com.khimii.medicalvisitscheduler.model.Patient;
import com.khimii.medicalvisitscheduler.model.Visit;
import com.khimii.medicalvisitscheduler.model.dto.PatientListResponse;
import com.khimii.medicalvisitscheduler.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @Test
    void shouldReturnPatientsWithVisits() {
        Pageable pageable = PageRequest.of(0, 10);
        Doctor doctor = new Doctor(1L, "John", "Doe", "UTC", 5);
        Patient patient = new Patient(1L, "Alice", "Smith");
        Visit visit = buildVisit(LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(9), doctor, patient);
        patient.setVisits(List.of(visit));

        Page<Patient> patientPage = new PageImpl<>(List.of(patient));
        when(patientRepository.findPatientsWithVisits(null, null, pageable)).thenReturn(patientPage);

        Optional<PatientListResponse> response = patientService.getPatients(0, 10, null, null);

        assertSoftly(softly -> {
            softly.assertThat(response).isPresent();
            softly.assertThat(response.get().getData()).hasSize(1);
            softly.assertThat(response.get().getData().get(0).getFirstName()).isEqualTo("Alice");
            softly.assertThat(response.get().getData().get(0).getLastVisits()).hasSize(1);
            softly.assertThat(response.get().getData().get(0).getLastVisits().get(0).getDoctor().getFirstName()).isEqualTo("John");
        });
    }

    @Test
    void shouldReturnEmptyWhenNoPatientsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(patientRepository.findPatientsWithVisits(null, null, pageable)).thenReturn(Page.empty());

        Optional<PatientListResponse> response = patientService.getPatients(0, 10, null, null);

        assertThat(response).isEmpty();
    }

    @Test
    void shouldFilterPatientsByName() {
        Pageable pageable = PageRequest.of(0, 10);
        Doctor doctor = new Doctor(1L, "John", "Doe", "UTC", 5);
        Patient patient = new Patient(1L, "Alice", "Smith");
        Visit visit = buildVisit(LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(9), doctor, patient);
        patient.setVisits(List.of(visit));

        Page<Patient> patientPage = new PageImpl<>(List.of(patient));
        when(patientRepository.findPatientsWithVisits("Alice", null, pageable)).thenReturn(patientPage);

        Optional<PatientListResponse> response = patientService.getPatients(0, 10, "Alice", null);

        assertThat(response).isPresent();
        assertThat(response.get().getData()).hasSize(1);
        assertThat(response.get().getData().get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void shouldFilterVisitsByDoctorIds() {
        Pageable pageable = PageRequest.of(0, 10);
        Doctor doctor = new Doctor(1L, "John", "Doe", "UTC", 5);
        Patient patient = new Patient(1L, "Alice", "Smith");
        Visit visit = buildVisit(LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(9), doctor, patient);
        patient.setVisits(List.of(visit));

        Page<Patient> patientPage = new PageImpl<>(List.of(patient));
        when(patientRepository.findPatientsWithVisits(null, List.of(1L), pageable)).thenReturn(patientPage);

        Optional<PatientListResponse> response = patientService.getPatients(0, 10, null, List.of(1L));

        assertThat(response).isPresent();
        assertThat(response.get().getData()).hasSize(1);
        assertThat(response.get().getData().get(0).getFirstName()).isEqualTo("Alice");
        assertThat(response.get().getData().get(0).getLastVisits()).hasSize(1);
        assertThat(response.get().getData().get(0).getLastVisits().get(0).getDoctor().getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldReturnEmptyWhenNoCompletedVisits() {
        Pageable pageable = PageRequest.of(0, 10);
        Doctor doctor = new Doctor(1L, "John", "Doe", "UTC", 5);
        Patient patient = new Patient(1L, "Alice", "Smith");
        Visit visit = buildVisit(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), doctor, patient);
        patient.setVisits(List.of(visit));

        Page<Patient> patientPage = new PageImpl<>(List.of(patient));
        when(patientRepository.findPatientsWithVisits(null, null, pageable)).thenReturn(patientPage);

        Optional<PatientListResponse> response = patientService.getPatients(0, 10, null, null);

        assertThat(response).isPresent();
        assertThat(response.get().getData()).isEmpty();
    }

    private Visit buildVisit(LocalDateTime startDateTime, LocalDateTime endDateTime, Doctor doctor, Patient patient) {
        return Visit.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .doctor(doctor)
                .patient(patient)
                .build();
    }
}