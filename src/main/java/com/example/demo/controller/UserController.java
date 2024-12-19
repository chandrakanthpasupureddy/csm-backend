package com.example.demo.controller;

import com.example.demo.entity.PatientDetails;
import com.example.demo.entity.User;
import com.example.demo.repository.PatientDetailsRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.PatientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientDetailsRepository patientDetailsRepository;
    @Autowired
    private PatientService patientService;

    private static final String SECRET_KEY = "my-secret-key";

    // HMAC-SHA256 Hashing for password
    private String hashPassword(String password) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    // HMAC-SHA256 Token Generation (for login and authentication)
    private String generateHmacSha256Token(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    @PostMapping("/register")
    public ApiResponse registerUser(@RequestBody User user) {
        try {
            user.setPassword(hashPassword(user.getPassword())); // Hash the password before saving
            userRepository.save(user);

            // Generate HMAC-SHA256 token after registration
            String token = generateHmacSha256Token(user.getEmail() + user.getPhone() + user.getPassword());

            return new ApiResponse("Registration successful", token); // Return success message and token
        } catch (Exception e) {
            return new ApiResponse("Error during registration", null); // Return error message if an exception occurs
        }
    }

    @PostMapping("/login")
    public ApiResponse loginUser(@RequestBody User loginData) {
        try {
            Optional<User> user = userRepository.findByEmail(loginData.getEmail());
            if (user.isPresent()) {
                String hashedPassword = hashPassword(loginData.getPassword());
                if (hashedPassword.equals(user.get().getPassword())) {
                    // If login is successful, generate token for authentication
                    String token = generateHmacSha256Token(user.get().getEmail() + user.get().getPhone() + user.get().getPassword());
                    return new ApiResponse("Login successful", token); // Return success message and token
                }
            }
            return new ApiResponse("Invalid credentials", null); // Return failure message if login fails
        } catch (Exception e) {
            return new ApiResponse("Login failed", null); // Return failure message
        }
    }

    @PostMapping("/book-appointment")
    public ApiResponse bookAppointment(@RequestBody PatientDetails patientDetails) {
        try {

            // Save the patient details
            patientDetailsRepository.save(patientDetails);

            return new ApiResponse("Appointment booked successfully", patientDetails.getToken());
        } catch (Exception e) {
            return new ApiResponse("Failed to book appointment", null);
        }
    }

    @GetMapping("/protected")
    public ApiResponse protectedRoute(@RequestHeader("Authorization") String token, @RequestBody User user) {
        if (token != null && !token.isEmpty()) {
            String pass = user.getPassword();
            return new ApiResponse("Access granted", token); // Return success if the token is valid
        }
        return new ApiResponse("Access denied. Please log in.", null); // Return failure if no valid token
    }
    @GetMapping("/get-all-patient-details")
    public List<PatientDetails> getAllPatientDetails() {
        return patientService.getAllPatientDetails(); // Fetch all patient details
    }


}
