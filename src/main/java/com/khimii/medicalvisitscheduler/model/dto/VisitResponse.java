package com.khimii.medicalvisitscheduler.model.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitResponse {
    private String start;
    private String end;
    private DoctorResponse doctor;
}
