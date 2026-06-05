package com.rupesh.copilot.clinicalsapi.controllers;

import com.rupesh.copilot.clinicalsapi.exceptions.ResourceNotFoundException;
import com.rupesh.copilot.clinicalsapi.models.Patient;
import com.rupesh.copilot.clinicalsapi.repositories.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        log.info("Creating patient: {} {}", patient.getFirstName(), patient.getLastName());
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient created with id: {}", savedPatient.getId());
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        log.info("Fetching all patients");
        List<Patient> patients = patientRepository.findAll();
        log.info("Found {} patients", patients.size());
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        log.info("Fetching patient with id: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient not found with id: {}", id);
                    return new ResourceNotFoundException("Patient", id);
                });
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        log.info("Updating patient with id: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient not found with id: {}", id);
                    return new ResourceNotFoundException("Patient", id);
                });
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setAge(patientDetails.getAge());
        log.info("Patient with id: {} updated successfully", id);
        return new ResponseEntity<>(patientRepository.save(patient), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        log.info("Deleting patient with id: {}", id);
        if (!patientRepository.existsById(id)) {
            log.warn("Patient not found with id: {}", id);
            throw new ResourceNotFoundException("Patient", id);
        }
        patientRepository.deleteById(id);
        log.info("Patient with id: {} deleted successfully", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

