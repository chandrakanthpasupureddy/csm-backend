package com.example.demo.service;

import com.example.demo.entity.PatientDetails;
import com.example.demo.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // Method to fetch all patient details from the database
    public List<PatientDetails> getAllPatientDetails() {
        return patientRepository.findAll(); // This fetches all records from the database
    }

}
