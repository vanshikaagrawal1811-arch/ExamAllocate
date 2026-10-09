package com.examallocate.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ApplyRequest(
        @NotBlank @Size(max = 150) String courseName,
        @NotBlank @Size(max = 100) String name,
        @NotNull LocalDate dob,
        @NotBlank @Size(max = 20) String gender,
        @NotBlank @Size(max = 30) String category,
        @NotBlank @Size(max = 20) String phone,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 255) String address,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String state,
        @NotBlank @Size(max = 10) String pincode,
        @NotBlank @Size(max = 150) String schoolName,
        @NotBlank @Size(max = 100) String preferredCity1,
        @Size(max = 100) String preferredCity2,
        String accommodationType) {}
