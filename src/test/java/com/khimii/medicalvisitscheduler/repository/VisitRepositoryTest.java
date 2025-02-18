package com.khimii.medicalvisitscheduler.repository;

import com.khimii.medicalvisitscheduler.config.TestDatabaseConfig;
import com.khimii.medicalvisitscheduler.model.Doctor;
import com.khimii.medicalvisitscheduler.model.Patient;
import com.khimii.medicalvisitscheduler.model.Visit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestDatabaseConfig.class)
class VisitRepositoryTest {
    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VisitRepository visitRepository;

    @BeforeAll
    static void setup() {
        mysql.start();
    }

    @AfterAll
    static void tearDown() {
        mysql.stop();
    }

    @Test
    void shouldReturnTrueWhenConflictingVisitExists() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setTimezone("UTC");
        entityManager.persist(doctor);

        Patient patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Smith");
        entityManager.persist(patient);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        Visit visit = Visit.builder()
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .patient(patient)
                .build();
        entityManager.persist(visit);

        entityManager.flush();

        boolean exists = visitRepository.existsConflictingVisit(doctor.getId(), start.plusMinutes(30), end.plusMinutes(30));

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoConflictingVisit() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setTimezone("UTC");
        entityManager.persist(doctor);

        Patient patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Smith");
        entityManager.persist(patient);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        Visit visit = Visit.builder()
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .patient(patient)
                .build();
        entityManager.persist(visit);

        entityManager.flush();

        boolean exists = visitRepository.existsConflictingVisit(doctor.getId(), end.plusHours(1), end.plusHours(2));

        assertThat(exists).isFalse();
    }

    @Test
    void shouldLockVisitWithOptimisticLocking() {
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
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .doctor(doctor)
                .patient(patient)
                .build();
        entityManager.persist(visit);
        entityManager.flush();

        Optional<Visit> lockedVisit = visitRepository.findById(visit.getId());

        assertThat(lockedVisit).isPresent();
        assertThat(lockedVisit.get().getVersion()).isNotNull();
    }
}

