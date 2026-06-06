# Clinicals API

A Spring Boot REST API for managing patient health records and clinical data (blood pressure, heart rate, etc.).

---

## Overview

**Clinicals API** is a backend service that allows you to:
- ✅ Create, read, update, and delete patient records
- ✅ Store and manage clinical measurements (e.g., BP, heart rate) for each patient
- ✅ Query clinical data by patient or by measurement type
- ✅ Handle errors gracefully with consistent error responses
- ✅ Log all API activities for debugging and monitoring

---

## Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (React)                         │
│                  http://localhost:3000                       │
└────────────────────────────┬────────────────────────────────┘
                             │
                    CORS Enabled (Port 3000)
                             │
┌────────────────────────────▼────────────────────────────────┐
│                    Clinicals API (Spring Boot)               │
│                   http://localhost:8080                      │
├─────────────────────────────────────────────────────────────┤
│  Controllers                                                 │
│  ├─ PatientController       (/api/patients)                 │
│  └─ ClinicalDataController  (/api/clinicaldata)            │
│                                                              │
│  Services & Repositories                                     │
│  ├─ PatientRepository       (JPA)                           │
│  └─ ClinicalDataRepository  (JPA)                          │
│                                                              │
│  Exception Handling                                          │
│  └─ GlobalExceptionHandler  (@RestControllerAdvice)        │
└────────────────────────────┬────────────────────────────────┘
                             │
              JDBC (PostgreSQL Driver)
                             │
┌────────────────────────────▼────────────────────────────────┐
│                PostgreSQL Database                           │
│                localhost:5432/clinicals                     │
├─────────────────────────────────────────────────────────────┤
│  Tables                                                      │
│  ├─ patient                                                  │
│  │  ├─ id (PK)                                              │
│  │  ├─ first_name                                           │
│  │  ├─ last_name                                            │
│  │  └─ age                                                   │
│  │                                                           │
│  └─ clinical_data                                            │
│     ├─ id (PK)                                              │
│     ├─ patient_id (FK)                                      │
│     ├─ component_name (e.g., "bp", "heartrate")            │
│     ├─ component_value (e.g., "120/80", "72")              │
│     └─ measured_date_time                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────────┐           ┌─────────────────────┐
│     Patient      │           │   ClinicalData      │
├──────────────────┤           ├─────────────────────┤
│ id (PK)          │──────1◄──╋──| patient_id (FK)  │
│ first_name       │   1   *  │ id (PK)             │
│ last_name        │          │ component_name      │
│ age              │          │ component_value     │
└──────────────────┘          │ measured_date_time  │
      ▲                        └─────────────────────┘
      │
   One Patient
   has many
   ClinicalData records
```

---

## API Endpoints

### Patient Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/patients` | Get all patients |
| `GET` | `/api/patients/{id}` | Get patient by ID |
| `POST` | `/api/patients` | Create new patient |
| `PUT` | `/api/patients/{id}` | Update patient |
| `DELETE` | `/api/patients/{id}` | Delete patient |

### Clinical Data Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/clinicaldata` | Get all clinical data |
| `GET` | `/api/clinicaldata/{id}` | Get clinical data by ID |
| `GET` | `/api/clinicaldata/patient/{patientId}` | Get data for a patient |
| `POST` | `/api/clinicaldata/clinicals` | Create clinical data for patient |
| `PUT` | `/api/clinicaldata/{id}` | Update clinical data |
| `DELETE` | `/api/clinicaldata/{id}` | Delete clinical data |

---

## Quick Start

### Prerequisites
- Java 21+
- PostgreSQL 12+
- Maven 3.6+

### Setup

1. **Create Database**
```sql
create database clinicals;
use clinicals;

CREATE TABLE patient (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    age int
);

CREATE TABLE clinical_data (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    patient_id int NOT NULL,
    component_name varchar(255) NOT NULL,
    component_value varchar(255) NOT NULL,
    measured_date_time TIMESTAMP,
    CONSTRAINT fk_patient FOREIGN KEY (patient_id) REFERENCES patient(id)
);
```

2. **Configure Database** (in `application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clinicals
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. **Build & Run**
```bash
mvn clean install
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

---

## Usage Examples

### Create a Patient
```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","age":30}'
```

### Create Clinical Data for Patient
```bash
curl -X POST http://localhost:8080/api/clinicaldata/clinicals \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "componentName": "bp",
    "componentValue": "120/80"
  }'
```

### Fetch All Patients
```bash
curl http://localhost:8080/api/patients
```

---

## Logging

Logs are printed to console at **DEBUG** level for `com.rupesh.copilot.clinicalsapi` package.

To enable file logging, uncomment in `application.properties`:
```properties
logging.file.name=logs/clinicalsapi.log
```

Sample log output:
```
2026-06-05 17:50:00 [http-nio-8080-exec-1] INFO  PatientController - Creating patient: John Doe
2026-06-05 17:50:00 [http-nio-8080-exec-1] INFO  PatientController - Patient created with id: 1
```

---

## Error Handling

All errors return a structured response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Patient not found with id: 999",
  "timestamp": "2026-06-05T17:45:32"
}
```

---

## CORS Configuration

Frontend on `http://localhost:3000` is allowed to call this API. Configure in `CorsConfig.java`:

```java
registry.addMapping("/api/**")
    .allowedOrigins("http://localhost:3000")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("*");
```

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 4.0.6 |
| **Language** | Java 21 |
| **Database** | PostgreSQL |
| **ORM** | JPA/Hibernate |
| **Testing** | JUnit 5 + Mockito |
| **Build** | Maven |

---

## Project Structure

```
clinicalsapi/
├── src/
│   ├── main/
│   │   ├── java/com/rupesh/copilot/clinicalsapi/
│   │   │   ├── controllers/        (REST endpoints)
│   │   │   ├── models/             (JPA entities)
│   │   │   ├── repositories/       (Data access)
│   │   │   ├── exceptions/         (Error handling)
│   │   │   ├── dto/                (Data transfer objects)
│   │   │   ├── config/             (CORS, logging config)
│   │   │   └── ClinicalsApiApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/clinicals.sql     (Database schema)
│   └── test/
│       └── java/...controllers/    (Unit & MVC tests)
├── pom.xml
└── README.md
```

---

## Testing

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=PatientControllerTest
```

---

## Future Enhancements

- 📊 Add analytics endpoints (average BP, trends)
- 🔐 Add authentication & authorization (JWT)
- 📱 Add pagination for large result sets
- 🔍 Add search & filtering capabilities
- 📈 Add data export (CSV, PDF)

---

## License

MIT License

---

## Author

Rupesh Patil

