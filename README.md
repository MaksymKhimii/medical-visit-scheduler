# Medical Visit Scheduler

## Overview

The **Medical Visit Scheduler** is a Spring Boot application designed to manage medical appointments between patients and doctors. It provides RESTful APIs to schedule visit. The application uses Flyway for database migrations and Docker for easy setup and deployment.

### Key Features:
- Schedule visits and check for conflicts between doctors' appointments.
- Optimistic locking to prevent race conditions when scheduling visits.
- Docker Compose support for quick setup and running.
- Swagger UI for easy API documentation and testing.

---

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17**
- **Maven**
- **Docker**

---

## Running the Application with Docker Compose

To run the application using Docker Compose, follow these steps:

1. **Clone the repository:**
   ```bash
   git clone git@github.com:MaksymKhimii/medical-visit-scheduler.git
   
   cd medical-visit-scheduler

2. **Build the application:**
   ```bash
   mvn clean package

3. **Run Docker Compose:**
   ```bash
   docker-compose up --build
This command will:
- Start the MySQL container.
- Build and start the Spring Boot application.

## Access the application:

- The application will be available at [http://localhost:8080](http://localhost:8080)
- Swagger UI will be available at [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)

## Example API Endpoints:

- **GET `/api/v1/patients`**: Retrieves a paginated list of patients who have completed visits. The list can be filtered by search term (patient's first name) and/or a list of doctor IDs.
  - **Query Parameters**:
    - `page`: The page number for pagination (default is 0).
    - `size`: The number of records per page (default is 10).
    - `search`: Optional search term for filtering patients by their first name.
    - `doctorIds`: Optional list of doctor IDs to filter patients by specific doctors.

- **POST `/api/v1/visits`**: Schedules a new visit for a patient with a doctor.
  - **Request Body**:
    - `patientId`: The ID of the patient.
    - `doctorId`: The ID of the doctor.
    - `startDateTime`: The start date and time of the visit.
    - `endDateTime`: The end date and time of the visit.

## Database Migrations and Dump

- **Database Migrations:** All database schema changes are managed via Flyway. You can find the migration scripts in the `src/main/resources/db/migration` folder.
- **Using a Database Dump:** You can find a dump of the database in the `db/Dump_health_tracking.sql` folder.

---

## Testing

The application includes various types of tests to ensure its functionality and reliability:

### Unit Tests

Unit tests are written to cover the core logic of the application. They are executed using **JUnit** and **Mockito** to mock dependencies and verify individual components' behavior.

### Integration Tests

Integration tests are used to test the interactions between the components of the application and the database. The application uses **Testcontainers** to spin up MySQL containers for testing the persistence layer.

#### Running Tests

To run all the tests, execute the following command:
```bash
mvn test
