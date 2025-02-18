package com.khimii.medicalvisitscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {
    private String firstName;
    private String lastName;
    private List<VisitResponse> lastVisits;
}
