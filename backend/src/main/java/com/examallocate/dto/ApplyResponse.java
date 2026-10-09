package com.examallocate.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ApplyResponse(
        Long regId,
        Long studentId,
        String applicationNo,
        String courseName,
        LocalDate examDate,
        String shiftName,
        LocalTime startTime,
        LocalTime endTime,
        String message) {}
