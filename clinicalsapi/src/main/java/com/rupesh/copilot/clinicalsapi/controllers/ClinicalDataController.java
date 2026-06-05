package com.rupesh.copilot.clinicalsapi.controllers;

import com.rupesh.copilot.clinicalsapi.exceptions.ResourceNotFoundException;
import com.rupesh.copilot.clinicalsapi.models.ClinicalData;
import com.rupesh.copilot.clinicalsapi.models.Patient;
import com.rupesh.copilot.clinicalsapi.repositories.ClinicalDataRepository;
import com.rupesh.copilot.clinicalsapi.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clinicaldata")
public class ClinicalDataController {

    @Autowired
    private ClinicalDataRepository clinicalDataRepository;

    @Autowired
    private PatientRepository patientRepository;

    // Create - POST
    @PostMapping
    public ResponseEntity<ClinicalData> createClinicalData(@RequestBody ClinicalData clinicalData) {
        ClinicalData saved = clinicalDataRepository.save(clinicalData);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // Read - GET all
    @GetMapping
    public ResponseEntity<List<ClinicalData>> getAllClinicalData() {
        List<ClinicalData> dataList = clinicalDataRepository.findAll();
        return new ResponseEntity<>(dataList, HttpStatus.OK);
    }

    // Read - GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClinicalData> getClinicalDataById(@PathVariable Long id) {
        ClinicalData clinicalData = clinicalDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalData", id));
        return new ResponseEntity<>(clinicalData, HttpStatus.OK);
    }

    // Update - PUT
    @PutMapping("/{id}")
    public ResponseEntity<ClinicalData> updateClinicalData(@PathVariable Long id,
               @RequestBody ClinicalData clinicalData) {
        clinicalDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalData", id));
        clinicalData.setId(id);
        ClinicalData updated = clinicalDataRepository.save(clinicalData);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Delete - DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinicalData(@PathVariable Long id) {
        clinicalDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalData", id));
        clinicalDataRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // add endpoint to create specific patient clinical data
    @PostMapping("/patient/{patientId}")
    public ResponseEntity<ClinicalData> createClinicalDataForPatient(@PathVariable Long patientId,
                     @RequestBody ClinicalData clinicalData) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        ClinicalData newClinicalData = new ClinicalData();
        newClinicalData.setPatient(patient);
        newClinicalData.setComponentName(clinicalData.getComponentName());
        newClinicalData.setComponentValue(clinicalData.getComponentValue());
        newClinicalData.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));
        return new ResponseEntity<>(clinicalDataRepository.save(newClinicalData), HttpStatus.CREATED);
    }
}

