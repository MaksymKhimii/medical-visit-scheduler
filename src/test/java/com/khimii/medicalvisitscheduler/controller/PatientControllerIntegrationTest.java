
package com.khimii.medicalvisitscheduler.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "/V1__initial_migration_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PatientControllerIntegrationTest {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8")
            .withDatabaseName("medical_visits")
            .withUsername("root")
            .withPassword("root");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Test
    void shouldReturnAllPatients() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.data[0].lastVisits.length()").value(1));
    }

    @Test
    void shouldReturnPatientsFilteredByName() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.data[0].lastVisits.length()").value(1));
    }

    @Test
    void shouldReturnPatientsFilteredByDoctorId() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("doctorIds", "1,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.data[0].lastVisits.length()").value(1));
    }

    @Test
    void shouldReturnEmptyListWhenNoPatientsFound() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "NonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInvalidPageOrSize() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.count").value(5));
    }
}
