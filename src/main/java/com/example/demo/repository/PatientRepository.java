package com.example.demo.repository;

import com.example.demo.entity.PatientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<PatientDetails, Long> {
    // JpaRepository provides findAll() method automatically
}
