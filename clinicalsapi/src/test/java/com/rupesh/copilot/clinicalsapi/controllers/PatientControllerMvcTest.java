package com.rupesh.copilot.clinicalsapi.controllers;

import com.rupesh.copilot.clinicalsapi.exceptions.GlobalExceptionHandler;
import com.rupesh.copilot.clinicalsapi.models.Patient;
import com.rupesh.copilot.clinicalsapi.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerMvcTest {

    private MockMvc mockMvc;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientController patientController;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

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
    void getAllPatients_ReturnsListOfPatients() throws Exception {
        List<Patient> patients = Arrays.asList(patient1, patient2);
        when(patientRepository.findAll()).thenReturn(patients);

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk());

        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getAllPatients_ReturnsEmptyListWhenNoPatients() throws Exception {
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk());

        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getPatientById_ReturnsPatientWhenFound() throws Exception {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk());

        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientById_Returns404WhenNotFound() throws Exception {
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/999"))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(999L);
    }

    @Test
    void createPatient_ReturnsCreatedPatient() throws Exception {

        Patient savedPatient = new Patient();
        savedPatient.setId(3L);
        savedPatient.setFirstName("Bob");
        savedPatient.setLastName("Johnson");
        savedPatient.setAge(35);

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        String patientJson = "{\"firstName\":\"Bob\",\"lastName\":\"Johnson\",\"age\":35}";

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isCreated());

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void updatePatient_ReturnsUpdatedPatientWhenFound() throws Exception {
        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setFirstName("John");
        updatedPatient.setLastName("Doe Updated");
        updatedPatient.setAge(31);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        String updateJson = "{\"firstName\":\"John\",\"lastName\":\"Doe Updated\",\"age\":31}";

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void updatePatient_Returns404WhenNotFound() throws Exception {
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        String updateJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":30}";

        mockMvc.perform(put("/api/patients/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(999L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void deletePatient_ReturnsNoContentWhenDeleted() throws Exception {
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());

        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePatient_Returns404WhenNotFound() throws Exception {
        when(patientRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/patients/999"))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).existsById(999L);
        verify(patientRepository, never()).deleteById(anyLong());
    }
}
