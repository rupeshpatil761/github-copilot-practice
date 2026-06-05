package com.rupesh.copilot.clinicalsapi.controllers;

import com.rupesh.copilot.clinicalsapi.exceptions.ResourceNotFoundException;
import com.rupesh.copilot.clinicalsapi.models.Patient;
import com.rupesh.copilot.clinicalsapi.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientController patientController;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        patient1 = new Patient();
        patient1.setId(1L);
        patient1.setFirstName("John");
        patient1.setLastName("Doe");
        patient1.setAge(30);

        patient2 = new Patient();
        patient2.setId(2L);
        patient2.setFirstName("Jane");
        patient2.setLastName("Smith");
        patient2.setAge(25);
    }

    @Test
    void testGetAllPatients() {
        List<Patient> patients = Arrays.asList(patient1, patient2);
        when(patientRepository.findAll()).thenReturn(patients);

        ResponseEntity<List<Patient>> response = patientController.getAllPatients();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());
        assertEquals("Jane", response.getBody().get(1).getFirstName());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void testGetPatientById_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));

        ResponseEntity<Patient> response = patientController.getPatientById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals(30, response.getBody().getAge());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPatientById_NotFound() {
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientController.getPatientById(999L));

        verify(patientRepository, times(1)).findById(999L);
    }

    @Test
    void testCreatePatient() {
        Patient newPatient = new Patient();
        newPatient.setFirstName("Bob");
        newPatient.setLastName("Johnson");
        newPatient.setAge(35);

        Patient savedPatient = new Patient();
        savedPatient.setId(3L);
        savedPatient.setFirstName("Bob");
        savedPatient.setLastName("Johnson");
        savedPatient.setAge(35);

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        ResponseEntity<Patient> response = patientController.createPatient(newPatient);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getId());
        assertEquals("Bob", response.getBody().getFirstName());
        assertEquals("Johnson", response.getBody().getLastName());
        assertEquals(35, response.getBody().getAge());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void testUpdatePatient_Success() {
        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setFirstName("John");
        updatedPatient.setLastName("Doe Updated");
        updatedPatient.setAge(31);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        ResponseEntity<Patient> response = patientController.updatePatient(1L, updatedPatient);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Doe Updated", response.getBody().getLastName());
        assertEquals(31, response.getBody().getAge());
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void testUpdatePatient_NotFound() {
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> patientController.updatePatient(999L, patient1));
        verify(patientRepository, times(1)).findById(999L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testDeletePatient_Success() {
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        ResponseEntity<Void> response = patientController.deletePatient(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePatient_NotFound() {
        when(patientRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> patientController.deletePatient(999L));
        verify(patientRepository, times(1)).existsById(999L);
        verify(patientRepository, never()).deleteById(anyLong());
    }
}



















