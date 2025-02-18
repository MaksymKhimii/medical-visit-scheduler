package com.khimii.medicalvisitscheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.khimii.medicalvisitscheduler.model.dto.VisitRequest;
import com.khimii.medicalvisitscheduler.service.VisitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VisitControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VisitService visitService;

    @InjectMocks
    private VisitController visitController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
    }

    @Test
    void shouldCreateVisitSuccessfully() throws Exception {
        VisitRequest request = new VisitRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setStart(LocalDateTime.of(2023, 10, 10, 10, 0));
        request.setEnd(LocalDateTime.of(2023, 10, 10, 11, 0));

        doNothing().when(visitService).createVisit(any(VisitRequest.class));

        mockMvc.perform(post("/api/v1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Visit created successfully"));
    }


    @Test
    void shouldReturnBadRequestWhenDoctorNotFound() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();

        VisitRequest request = new VisitRequest();
        request.setPatientId(1L);
        request.setDoctorId(999L);
        request.setStart(LocalDateTime.of(2023, 10, 10, 10, 0));
        request.setEnd(LocalDateTime.of(2023, 10, 10, 11, 0));

        doThrow(new RuntimeException("Doctor with ID 999 not found"))
                .when(visitService).createVisit(any(VisitRequest.class));

        mockMvc.perform(post("/api/v1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Doctor with ID 999 not found"));
    }

    @Test
    void shouldReturnBadRequestWhenPatientNotFound() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();

        VisitRequest request = new VisitRequest();
        request.setPatientId(999L);
        request.setDoctorId(1L);
        request.setStart(LocalDateTime.of(2023, 10, 10, 10, 0));
        request.setEnd(LocalDateTime.of(2023, 10, 10, 11, 0));

        doThrow(new RuntimeException("Patient with ID 999 not found"))
                .when(visitService).createVisit(any(VisitRequest.class));

        mockMvc.perform(post("/api/v1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Patient with ID 999 not found"));
    }

    @Test
    void shouldReturnBadRequestWhenConflictingVisit() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();

        VisitRequest request = new VisitRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setStart(LocalDateTime.of(2023, 10, 10, 10, 0));
        request.setEnd(LocalDateTime.of(2023, 10, 10, 11, 0));

        doThrow(new RuntimeException("Doctor is not available at this time"))
                .when(visitService).createVisit(any(VisitRequest.class));

        mockMvc.perform(post("/api/v1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Doctor is not available at this time"));
    }

    @Test
    void shouldReturnBadRequestWhenOptimisticLockException() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();

        VisitRequest request = new VisitRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setStart(LocalDateTime.of(2023, 10, 10, 10, 0));
        request.setEnd(LocalDateTime.of(2023, 10, 10, 11, 0));

        doThrow(new RuntimeException("Concurrent modification detected. Please try again."))
                .when(visitService).createVisit(any(VisitRequest.class));

        mockMvc.perform(post("/api/v1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Concurrent modification detected. Please try again."));
    }
}