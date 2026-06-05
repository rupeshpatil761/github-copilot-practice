package com.rupesh.copilot.clinicalsapi.controllers;

import com.rupesh.copilot.clinicalsapi.exceptions.ResourceNotFoundException;
import com.rupesh.copilot.clinicalsapi.models.Patient;
import com.rupesh.copilot.clinicalsapi.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient savedPatient = patientRepository.save(patient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setAge(patientDetails.getAge());
        return new ResponseEntity<>(patientRepository.save(patient), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient", id);
        }
        patientRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

