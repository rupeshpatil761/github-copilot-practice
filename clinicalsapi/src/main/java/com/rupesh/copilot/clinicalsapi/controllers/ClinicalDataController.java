package com.rupesh.copilot.clinicalsapi.controllers;

import com.rupesh.copilot.clinicalsapi.dto.ClinicalDataRequest;
import com.rupesh.copilot.clinicalsapi.exceptions.ResourceNotFoundException;
import com.rupesh.copilot.clinicalsapi.models.ClinicalData;
import com.rupesh.copilot.clinicalsapi.models.Patient;
import com.rupesh.copilot.clinicalsapi.repositories.ClinicalDataRepository;
import com.rupesh.copilot.clinicalsapi.repositories.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/clinicaldata")
public class ClinicalDataController {

    private static final Logger log = LoggerFactory.getLogger(ClinicalDataController.class);

    @Autowired
    private ClinicalDataRepository clinicalDataRepository;

    @Autowired
    private PatientRepository patientRepository;

    // Read - GET all
    @GetMapping
    public ResponseEntity<List<ClinicalData>> getAllClinicalData() {
        log.info("Fetching all clinical data");
        List<ClinicalData> dataList = clinicalDataRepository.findAll();
        log.info("Found {} clinical data records", dataList.size());
        return new ResponseEntity<>(dataList, HttpStatus.OK);
    }

    // Read - GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClinicalData> getClinicalDataById(@PathVariable Long id) {
        log.info("Fetching clinical data with id: {}", id);
        ClinicalData clinicalData = clinicalDataRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Clinical data not found with id: {}", id);
                    return new ResourceNotFoundException("ClinicalData", id);
                });
        return new ResponseEntity<>(clinicalData, HttpStatus.OK);
    }

    // Read - GET by Patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ClinicalData>> getClinicalDataByPatientId(@PathVariable Long patientId) {
        log.info("Fetching clinical data for patient id: {}", patientId);
        patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.warn("Patient not found with id: {}", patientId);
                    return new ResourceNotFoundException("Patient", patientId);
                });
        List<ClinicalData> dataList = clinicalDataRepository.findByPatientId(patientId);
        log.info("Found {} clinical data records for patient id: {}", dataList.size(), patientId);
        return new ResponseEntity<>(dataList, HttpStatus.OK);
    }

    // Update - PUT
    @PutMapping("/{id}")
    public ResponseEntity<ClinicalData> updateClinicalData(@PathVariable Long id,
               @RequestBody ClinicalData clinicalData) {
        log.info("Updating clinical data with id: {}", id);
        clinicalDataRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Clinical data not found with id: {}", id);
                    return new ResourceNotFoundException("ClinicalData", id);
                });
        clinicalData.setId(id);
        ClinicalData updated = clinicalDataRepository.save(clinicalData);
        log.info("Clinical data with id: {} updated successfully", id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Delete - DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinicalData(@PathVariable Long id) {
        log.info("Deleting clinical data with id: {}", id);
        clinicalDataRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Clinical data not found with id: {}", id);
                    return new ResourceNotFoundException("ClinicalData", id);
                });
        clinicalDataRepository.deleteById(id);
        log.info("Clinical data with id: {} deleted successfully", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/clinicals")
    public ResponseEntity<ClinicalData> createClinicalDataForPatient(@RequestBody ClinicalDataRequest clinicalDataRequest) {
        log.info("Creating clinical data for patient id: {}", clinicalDataRequest.getPatientId());
        Patient patient = patientRepository.findById(clinicalDataRequest.getPatientId())
                .orElseThrow(() -> {
                    log.warn("Patient not found with id: {}", clinicalDataRequest.getPatientId());
                    return new ResourceNotFoundException("Patient", clinicalDataRequest.getPatientId());
                });
        ClinicalData newClinicalData = new ClinicalData();
        newClinicalData.setPatient(patient);
        newClinicalData.setComponentName(clinicalDataRequest.getComponentName());
        newClinicalData.setComponentValue(clinicalDataRequest.getComponentValue());
        newClinicalData.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));
        ClinicalData saved = clinicalDataRepository.save(newClinicalData);
        log.info("Clinical data created with id: {} for patient id: {}", saved.getId(), clinicalDataRequest.getPatientId());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
