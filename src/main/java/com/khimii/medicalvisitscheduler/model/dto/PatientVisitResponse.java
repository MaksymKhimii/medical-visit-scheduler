package com.khimii.medicalvisitscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * represents response after searching patients in database
 */
@Getter
@AllArgsConstructor
public class PatientVisitResponse {
    private Long patientId;
    private String firstName;
    private String lastName;
    private LocalDateTime visitStart;
    private LocalDateTime visitEnd;
    private Long doctorId;
    private String doctorFirstName;
    private String doctorLastName;
    private Long totalPatients;
}
