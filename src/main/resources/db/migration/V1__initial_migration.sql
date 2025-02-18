SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS visits;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE doctors (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         first_name VARCHAR(255) NOT NULL,
                         last_name VARCHAR(255) NOT NULL,
                         timezone VARCHAR(255) NOT NULL
);

CREATE INDEX idx_doctor_timezone ON doctors(timezone);

CREATE TABLE patients (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL
);

CREATE INDEX idx_patient_first_name ON patients(first_name);

CREATE TABLE visits (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        start_date_time DATETIME NOT NULL,
                        end_date_time DATETIME NOT NULL,
                        patient_id BIGINT NOT NULL,
                        doctor_id BIGINT NOT NULL,
                        version INT NOT NULL DEFAULT 0,
                        CONSTRAINT fk_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
                        CONSTRAINT fk_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
                        CONSTRAINT unique_doctor_visit UNIQUE (doctor_id, start_date_time)
);

CREATE INDEX idx_doctor_id ON visits(doctor_id);
CREATE INDEX idx_start_end_time ON visits(start_date_time, end_date_time);

INSERT INTO doctors (first_name, last_name, timezone)
VALUES
    ('John', 'Doe', 'America/New_York'),
    ('Emily', 'Smith', 'Europe/London'),
    ('Carlos', 'Gomez', 'Asia/Tokyo'),
    ('Anna', 'Lee', 'Australia/Sydney'),
    ('Michael', 'Brown', 'Europe/Berlin');

INSERT INTO patients (first_name, last_name)
VALUES
    ('Alice', 'Brown'),
    ('Bob', 'Johnson'),
    ('Charlie', 'Davis'),
    ('David', 'Wilson'),
    ('Eve', 'Miller'),
    ('Frank', 'White'),
    ('Grace', 'Harris'),
    ('Henry', 'Clark'),
    ('Ivy', 'Lewis'),
    ('Jack', 'Walker');

INSERT INTO visits (start_date_time, end_date_time, patient_id, doctor_id)
VALUES
    ('2025-02-10 14:00:00', '2025-02-10 14:30:00', 1, 1),
    ('2025-02-12 10:00:00', '2025-02-12 10:30:00', 1, 2),
    ('2025-02-14 08:00:00', '2025-02-14 08:30:00', 1, 3),
    ('2025-02-11 16:00:00', '2025-02-11 16:30:00', 2, 1),
    ('2025-02-13 18:00:00', '2025-02-13 18:30:00', 2, 3),
    ('2025-02-12 17:00:00', '2025-02-12 17:30:00', 3, 2),
    ('2025-02-15 09:00:00', '2025-02-15 09:30:00', 4, 3),
    ('2025-02-16 11:00:00', '2025-02-16 11:30:00', 5, 4),
    ('2025-02-17 12:00:00', '2025-02-17 12:30:00', 5, 5),
    ('2025-02-18 13:00:00', '2025-02-18 13:30:00', 6, 1),
    ('2025-02-19 14:00:00', '2025-02-19 14:30:00', 6, 5),
    ('2025-02-20 15:00:00', '2025-02-20 15:30:00', 7, 2),
    ('2025-02-21 16:00:00', '2025-02-21 16:30:00', 8, 3),
    ('2025-02-22 17:00:00', '2025-02-22 17:30:00', 8, 4),
    ('2025-02-23 18:00:00', '2025-02-23 18:30:00', 9, 5),
    ('2025-02-24 19:00:00', '2025-02-24 19:30:00', 10, 1),
    ('2025-02-25 20:00:00', '2025-02-25 20:30:00', 10, 4);
