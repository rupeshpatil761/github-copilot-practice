package com.rupesh.copilot.clinicalsapi.controllers;

import com.rupesh.copilot.clinicalsapi.exceptions.GlobalExceptionHandler;
import com.rupesh.copilot.clinicalsapi.models.ClinicalData;
import com.rupesh.copilot.clinicalsapi.models.Patient;
import com.rupesh.copilot.clinicalsapi.repositories.ClinicalDataRepository;
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

import java.sql.Timestamp;
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
class ClinicalDataControllerMvcTest {

    private MockMvc mockMvc;

    @Mock
    private ClinicalDataRepository clinicalDataRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private ClinicalDataController clinicalDataController;

    private ClinicalData clinicalData1;
    private ClinicalData clinicalData2;
    private Patient patient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clinicalDataController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setAge(30);

        clinicalData1 = new ClinicalData();
        clinicalData1.setId(1L);
        clinicalData1.setPatient(patient);
        clinicalData1.setComponentName("bp");
        clinicalData1.setComponentValue("120/80");
        clinicalData1.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));

        clinicalData2 = new ClinicalData();
        clinicalData2.setId(2L);
        clinicalData2.setPatient(patient);
        clinicalData2.setComponentName("heartrate");
        clinicalData2.setComponentValue("72");
        clinicalData2.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void createClinicalData_ReturnsCreatedData() throws Exception {
        ClinicalData savedData = new ClinicalData();
        savedData.setId(3L);
        savedData.setComponentName("temperature");
        savedData.setComponentValue("98.6");

        when(clinicalDataRepository.save(any(ClinicalData.class))).thenReturn(savedData);

        String clinicalDataJson = "{\"componentName\":\"temperature\",\"componentValue\":\"98.6\"}";

        mockMvc.perform(post("/api/clinicaldata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clinicalDataJson))
                .andExpect(status().isCreated());

        verify(clinicalDataRepository, times(1)).save(any(ClinicalData.class));
    }

    @Test
    void getAllClinicalData_ReturnsListOfData() throws Exception {
        List<ClinicalData> dataList = Arrays.asList(clinicalData1, clinicalData2);
        when(clinicalDataRepository.findAll()).thenReturn(dataList);

        mockMvc.perform(get("/api/clinicaldata"))
                .andExpect(status().isOk());

        verify(clinicalDataRepository, times(1)).findAll();
    }

    @Test
    void getAllClinicalData_ReturnsEmptyListWhenNoData() throws Exception {
        when(clinicalDataRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/clinicaldata"))
                .andExpect(status().isOk());

        verify(clinicalDataRepository, times(1)).findAll();
    }

    @Test
    void getClinicalDataById_ReturnsDataWhenFound() throws Exception {
        when(clinicalDataRepository.findById(1L)).thenReturn(Optional.of(clinicalData1));

        mockMvc.perform(get("/api/clinicaldata/1"))
                .andExpect(status().isOk());

        verify(clinicalDataRepository, times(1)).findById(1L);
    }

    @Test
    void getClinicalDataById_Returns404WhenNotFound() throws Exception {
        when(clinicalDataRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clinicaldata/999"))
                .andExpect(status().isNotFound());

        verify(clinicalDataRepository, times(1)).findById(999L);
    }

    @Test
    void updateClinicalData_ReturnsUpdatedDataWhenFound() throws Exception {
        ClinicalData updatedData = new ClinicalData();
        updatedData.setId(1L);
        updatedData.setComponentName("bp");
        updatedData.setComponentValue("130/85");

        when(clinicalDataRepository.findById(1L)).thenReturn(Optional.of(clinicalData1));
        when(clinicalDataRepository.save(any(ClinicalData.class))).thenReturn(updatedData);

        String updateJson = "{\"id\":1,\"componentName\":\"bp\",\"componentValue\":\"130/85\"}";

        mockMvc.perform(put("/api/clinicaldata/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());

        verify(clinicalDataRepository, times(1)).findById(1L);
        verify(clinicalDataRepository, times(1)).save(any(ClinicalData.class));
    }

    @Test
    void updateClinicalData_Returns404WhenNotFound() throws Exception {
        when(clinicalDataRepository.findById(anyLong())).thenReturn(Optional.empty());

        String updateJson = "{\"id\":999,\"componentName\":\"bp\",\"componentValue\":\"130/85\"}";

        mockMvc.perform(put("/api/clinicaldata/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());

        verify(clinicalDataRepository, times(1)).findById(999L);
        verify(clinicalDataRepository, never()).save(any(ClinicalData.class));
    }

    @Test
    void deleteClinicalData_DeletesDataWhenFound() throws Exception {
        when(clinicalDataRepository.findById(1L)).thenReturn(Optional.of(clinicalData1));
        doNothing().when(clinicalDataRepository).deleteById(1L);

        mockMvc.perform(delete("/api/clinicaldata/1"))
                .andExpect(status().isNoContent());

        verify(clinicalDataRepository, times(1)).findById(1L);
        verify(clinicalDataRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteClinicalData_Returns404WhenNotFound() throws Exception {
        when(clinicalDataRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/clinicaldata/999"))
                .andExpect(status().isNotFound());

        verify(clinicalDataRepository, times(1)).findById(999L);
        verify(clinicalDataRepository, never()).deleteById(anyLong());
    }

    @Test
    void createClinicalDataForPatient_ReturnsCreatedDataWhenPatientFound() throws Exception {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(clinicalDataRepository.save(any(ClinicalData.class))).thenReturn(clinicalData1);

        String clinicalDataJson = "{\"componentName\":\"bp\",\"componentValue\":\"120/80\"}";

        mockMvc.perform(post("/api/clinicaldata/patient/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clinicalDataJson))
                .andExpect(status().isCreated());

        verify(patientRepository, times(1)).findById(1L);
        verify(clinicalDataRepository, times(1)).save(any(ClinicalData.class));
    }

    @Test
    void createClinicalDataForPatient_Returns404WhenPatientNotFound() throws Exception {
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        String clinicalDataJson = "{\"componentName\":\"bp\",\"componentValue\":\"120/80\"}";

        mockMvc.perform(post("/api/clinicaldata/patient/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clinicalDataJson))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(999L);
        verify(clinicalDataRepository, never()).save(any(ClinicalData.class));
    }
}

