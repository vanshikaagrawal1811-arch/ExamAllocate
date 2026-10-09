package com.examallocate.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AdmitCardResponse(String hallTicketNo, LocalDate issueDate, String studentName, String applicationNo,
                                String examName, LocalDate examDate, String shiftName, LocalTime startTime, LocalTime endTime,
                                String centerName, String address, String city, String roomNumber, Integer floor, Integer seatNumber) {}
