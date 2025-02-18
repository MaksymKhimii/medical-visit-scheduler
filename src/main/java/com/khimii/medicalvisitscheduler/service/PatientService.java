package com.khimii.medicalvisitscheduler.service;

import com.khimii.medicalvisitscheduler.model.Patient;
import com.khimii.medicalvisitscheduler.model.Visit;
import com.khimii.medicalvisitscheduler.model.dto.DoctorResponse;
import com.khimii.medicalvisitscheduler.model.dto.PatientListResponse;
import com.khimii.medicalvisitscheduler.model.dto.PatientResponse;
import com.khimii.medicalvisitscheduler.model.dto.VisitResponse;
import com.khimii.medicalvisitscheduler.repository.PatientRepository;
import com.khimii.medicalvisitscheduler.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Page<Patient> patientPage = patientRepository.findPatientsWithVisits(search, doctorIds, pageable);

        if (patientPage.isEmpty()) {
            log.warn("No patients found for search: '{}' and doctorIds: {}", search, doctorIds);
            return Optional.empty();
        }

        List<PatientResponse> patientResponses = patientPage.getContent().stream()
                .map(this::mapToPatientResponse)
                .filter(patient -> !patient.getLastVisits().isEmpty())
                .collect(Collectors.toList());

        log.info("Returning {} patients with completed visits", patientResponses.size());

        return Optional.of(new PatientListResponse(patientResponses, (int) patientPage.getTotalElements()));
    }

    /**
     * Maps a {@link Patient} entity to a {@link PatientResponse}.
     *
     * @param patient The patient entity.
     * @return A response object containing patient details and their last visits.
     */
    private PatientResponse mapToPatientResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setFirstName(patient.getFirstName());
        response.setLastName(patient.getLastName());

        Map<Long, Visit> latestVisits = getLatestVisitsForPatient(patient);

        List<VisitResponse> visitResponses = latestVisits.values().stream()
                .map(this::mapToVisitResponse)
                .collect(Collectors.toList());

        response.setLastVisits(visitResponses);
        return response;
    }

    /**
     * Retrieves the latest visits for a given patient, grouped by doctor.
     *
     * @param patient The patient entity.
     * @return A map where the key is the doctor ID and the value is the latest visit for that doctor.
     */
    private Map<Long, Visit> getLatestVisitsForPatient(Patient patient) {
        Map<Long, Visit> latestVisits = new HashMap<>();

        patient.getVisits().stream()
                .filter(visit -> visit.getEndDateTime().isBefore(LocalDateTime.now(ZoneId.of(visit.getDoctor().getTimezone()))))
                .forEach(visit -> {
                    Long doctorId = visit.getDoctor().getId();
                    latestVisits.merge(doctorId, visit, this::getLatestVisit);
                });

        return latestVisits;
    }

    /**
     * Determines the latest visit between two visits.
     *
     * @param existingVisit The existing visit.
     * @param newVisit      The new visit being compared.
     * @return The visit with the latest start time.
     */
    private Visit getLatestVisit(Visit existingVisit, Visit newVisit) {
        return newVisit.getStartDateTime().isAfter(existingVisit.getStartDateTime()) ? newVisit : existingVisit;
    }

    /**
     * Maps a {@link Visit} entity to a {@link VisitResponse}.
     *
     * @param visit The visit entity.
     * @return A response object containing visit details.
     */
    private VisitResponse mapToVisitResponse(Visit visit) {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setStart(DateUtil.convertUtcToDoctorTimezone(visit.getStartDateTime(), visit.getDoctor().getTimezone()));
        visitResponse.setEnd(DateUtil.convertUtcToDoctorTimezone(visit.getEndDateTime(), visit.getDoctor().getTimezone()));

        DoctorResponse doctorResponse = new DoctorResponse();
        doctorResponse.setFirstName(visit.getDoctor().getFirstName());
        doctorResponse.setLastName(visit.getDoctor().getLastName());
        doctorResponse.setTotalPatients(visit.getDoctor().getTotalPatients());

        visitResponse.setDoctor(doctorResponse);
        return visitResponse;
    }
}
