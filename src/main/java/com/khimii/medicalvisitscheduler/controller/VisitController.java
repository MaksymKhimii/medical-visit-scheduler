package com.khimii.medicalvisitscheduler.controller;

import com.khimii.medicalvisitscheduler.model.dto.VisitRequest;
import com.khimii.medicalvisitscheduler.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/visits")
@RequiredArgsConstructor
@Tag(name = "Visits", description = "Endpoints for managing medical visits")
public class VisitController {
    private final VisitService visitService;

    @PostMapping
    @Operation(summary = "Create a new visit", description = "Schedules a new visit for a patient with a doctor.")
    @ApiResponse(responseCode = "200", description = "Visit created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request or doctor unavailable",
            content = @Content(mediaType = "text/plain"))
    public ResponseEntity<String> createVisit(@RequestBody VisitRequest request) {
        try {
            visitService.createVisit(request);
            return ResponseEntity.ok("Visit created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

