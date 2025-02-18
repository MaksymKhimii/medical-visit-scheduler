package com.khimii.medicalvisitscheduler.repository;

import com.khimii.medicalvisitscheduler.config.TestDatabaseConfig;
import com.khimii.medicalvisitscheduler.model.Doctor;
import com.khimii.medicalvisitscheduler.model.Patient;
import com.khimii.medicalvisitscheduler.model.Visit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestDatabaseConfig.class)
class PatientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldFindPatientsWithCompletedVisits() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setTimezone("UTC");
        entityManager.persist(doctor);

        Patient patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Smith");
        entityManager.persist(patient);

        Visit visit = Visit.builder()
                .startDateTime(LocalDateTime.now().minusDays(10))
                .endDateTime(LocalDateTime.now().minusDays(9))
                .doctor(doctor)
                .patient(patient)
                .build();
        entityManager.persist(visit);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> result = patientRepository.findPatientsWithVisits(null, null, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(6);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void shouldFilterPatientsByDoctorId() {
        Doctor doctor1 = new Doctor();
        doctor1.setFirstName("John");
        doctor1.setLastName("Doe");
        doctor1.setTimezone("UTC");
        entityManager.persist(doctor1);

        Doctor doctor2 = new Doctor();
        doctor2.setFirstName("Jane");
        doctor2.setLastName("Doe");
        doctor2.setTimezone("UTC");
        entityManager.persist(doctor2);

        Patient patient1 = new Patient();
        patient1.setFirstName("Alice");
        patient1.setLastName("Smith");
        entityManager.persist(patient1);

        Patient patient2 = new Patient();
        patient2.setFirstName("Bob");
        patient2.setLastName("Brown");
        entityManager.persist(patient2);

        Visit visit1 = Visit.builder()
                .startDateTime(LocalDateTime.now().minusDays(10))
                .endDateTime(LocalDateTime.now().minusDays(9))
                .doctor(doctor1)
                .patient(patient1)
                .build();
        entityManager.persist(visit1);

        Visit visit2 = Visit.builder()
                .startDateTime(LocalDateTime.now().minusDays(8))
                .endDateTime(LocalDateTime.now().minusDays(7))
                .doctor(doctor2)
                .patient(patient2)
                .build();
        entityManager.persist(visit2);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<Long> doctorIds = List.of(doctor1.getId());

        Page<Patient> result = patientRepository.findPatientsWithVisits(null, doctorIds, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Alice");
    }
}
