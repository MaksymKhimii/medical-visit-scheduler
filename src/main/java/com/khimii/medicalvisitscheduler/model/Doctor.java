package com.khimii.medicalvisitscheduler.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

/**
 * Entity representing a doctor.
 */
@Entity
@Table(name = "doctors", indexes = @Index(columnList = "timezone"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String timezone;

    /**
     * The total number of unique patients the doctor has had visits with.
     * This is a derived value using a database formula.
     */
    @Formula("(SELECT COUNT(DISTINCT v.patient_id) FROM visits v WHERE v.doctor_id = id)")
    private int totalPatients;
}
