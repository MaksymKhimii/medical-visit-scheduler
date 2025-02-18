package com.khimii.medicalvisitscheduler.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a medical visit between a doctor and a patient.
 */
@Entity
@Table(name = "visits", uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "startDateTime"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version; // Optimistic locking version to prevent concurrent modification conflicts.

    @Column(nullable = false)
    private LocalDateTime startDateTime; // The start date and time of the visit, stored in UTC.

    @Column(nullable = false)
    private LocalDateTime endDateTime; // The end date and time of the visit, stored in UTC.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
}
