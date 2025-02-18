package com.khimii.medicalvisitscheduler.controller;

import com.khimii.medicalvisitscheduler.model.dto.PatientListResponse;
import com.khimii.medicalvisitscheduler.model.dto.PatientResponse;
import com.khimii.medicalvisitscheduler.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @Test
    void shouldReturnPatientsWithVisits() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        PatientResponse patientResponse = new PatientResponse();
        patientResponse.setFirstName("Alice");
        patientResponse.setLastName("Smith");
        patientResponse.setLastVisits(Collections.emptyList());

        PatientListResponse patientListResponse = new PatientListResponse(List.of(patientResponse), 1);

        when(patientService.getPatients(anyInt(), anyInt(), anyString(), anyList()))
                .thenReturn(Optional.of(patientListResponse));

        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "Alice")
                        .param("doctorIds", "1,2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.data[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenNoPatientsFound() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        when(patientService.getPatients(anyInt(), anyInt(), nullable(String.class), nullable(List.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidParameters() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }
}