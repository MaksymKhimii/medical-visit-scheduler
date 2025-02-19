package com.khimii.medicalvisitscheduler.service;

import com.khimii.medicalvisitscheduler.model.dto.*;
import com.khimii.medicalvisitscheduler.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Retrieves a paginated list of patients who have completed visits.
     *
     * @param page      The page number (default is 0).
     * @param size      The number of records per page (default is 10).
     * @param search    Optional search query to filter patients by name.
     * @param doctorIds Optional list of doctor IDs to filter visits by specific doctors.
     * @return A response containing a list of patients with their last completed visits.
     */
    public Optional<PatientListResponse> getPatients(int page, int size, String search, List<Long> doctorIds) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientVisitResponse> patientPage = patientRepository.findPatientsWithVisits(search, doctorIds, pageable);

        if (patientPage.isEmpty()) {
            log.warn("No patients found for search: '{}' and doctorIds: {}", search, doctorIds);
            return Optional.empty();
        }

        List<PatientResponse> patientResponses = patientPage.getContent().stream()
                .map(this::mapToPatientResponse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Returning {} patients with completed visits", patientResponses.size());

        return Optional.of(new PatientListResponse(patientResponses, (int) patientPage.getTotalElements()));
    }

    /**
     * Maps a PatientVisitResponse to a PatientResponse.
     *
     * @param patientVisitResponse The patient visit response after search.
     * @return A PatientResponse containing patient details and their last visits.
     */
    private PatientResponse mapToPatientResponse(PatientVisitResponse patientVisitResponse) {
        if (patientVisitResponse.getVisitEnd().isAfter(LocalDateTime.now())) {
            return null;
        }
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setStart(patientVisitResponse.getVisitStart().toString());
        visitResponse.setEnd(patientVisitResponse.getVisitEnd().toString());

        DoctorResponse doctorResponse = new DoctorResponse();
        doctorResponse.setFirstName(patientVisitResponse.getDoctorFirstName());
        doctorResponse.setLastName(patientVisitResponse.getDoctorLastName());
        doctorResponse.setTotalPatients(patientVisitResponse.getTotalPatients().intValue());

        visitResponse.setDoctor(doctorResponse);

        return new PatientResponse(
                patientVisitResponse.getFirstName(),
                patientVisitResponse.getLastName(),
                List.of(visitResponse)
        );
    }
}
