package com.examallocate.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record RegistrationLookupResponse(
        Long regId,
        String applicationNo,
        String studentName,
        String courseName,
        LocalDate examDate,
        String shiftName,
        LocalTime startTime,
        LocalTime endTime) {}
