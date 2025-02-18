package com.khimii.medicalvisitscheduler;

import com.khimii.medicalvisitscheduler.config.TestDatabaseConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestDatabaseConfig.Initializer.class)
class MedicalVisitSchedulerApplicationTests {

    @Test
    void contextLoads() {
    }

}
