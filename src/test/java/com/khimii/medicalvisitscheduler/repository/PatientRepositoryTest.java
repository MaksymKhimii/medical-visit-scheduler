package com.khimii.medicalvisitscheduler.repository;

import com.khimii.medicalvisitscheduler.config.TestDatabaseConfig;
import com.khimii.medicalvisitscheduler.model.dto.PatientVisitResponse;
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
    void shouldFindPatientsWithLastCompletedVisit() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PatientVisitResponse> result = patientRepository.findPatientsWithVisits(null, null, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Alice");
        assertThat(result.getContent().get(0).getVisitStart()).isBefore(LocalDateTime.now());
    }

    @Test
    void shouldFilterPatientsByName() {
        String searchQuery = "Alice";

        Pageable pageable = PageRequest.of(0, 10);
        Page<PatientVisitResponse> result = patientRepository.findPatientsWithVisits(searchQuery, null, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void shouldReturnEmptyIfNoPatientsMatch() {
        String searchQuery = "NonExistentName";

        Pageable pageable = PageRequest.of(0, 10);
        Page<PatientVisitResponse> result = patientRepository.findPatientsWithVisits(searchQuery, null, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCorrectlyCalculateTotalPatientsPerDoctor() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PatientVisitResponse> result = patientRepository.findPatientsWithVisits(null, null, pageable);

        assertThat(result).isNotEmpty();

        PatientVisitResponse firstPatient = result.getContent().get(0);
        assertThat(firstPatient.getTotalPatients()).isGreaterThan(0);
    }
}
