package com.khimii.medicalvisitscheduler.controller;

import com.khimii.medicalvisitscheduler.model.dto.PatientListResponse;
import com.khimii.medicalvisitscheduler.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Endpoints for managing patients")
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Get a list of patients", description = "Returns a paginated list of patients who have completed visits.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of patients retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No patients found matching the criteria",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<PatientListResponse> getPatients(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Page number (default is 0)") int page,

            @RequestParam(defaultValue = "10")
            @Parameter(description = "Page size (default is 10)") int size,

            @RequestParam(required = false)
            @Parameter(description = "Search term for filtering patients by first name") String search,

            @RequestParam(required = false)
            @Parameter(description = "List of doctor IDs to filter patients") List<Long> doctorIds) {
        if (page < 0 || size < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return patientService.getPatients(page, size, search, doctorIds)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}