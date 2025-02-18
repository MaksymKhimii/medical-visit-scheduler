package com.khimii.medicalvisitscheduler.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VisitRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long patientId;
    private Long doctorId;
}

