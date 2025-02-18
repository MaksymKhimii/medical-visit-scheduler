package com.khimii.medicalvisitscheduler.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorResponse {
    private String firstName;
    private String lastName;
    private int totalPatients;
}
