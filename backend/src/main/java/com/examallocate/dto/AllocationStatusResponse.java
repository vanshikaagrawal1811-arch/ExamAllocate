package com.examallocate.dto;

import java.time.LocalDateTime;

public record AllocationStatusResponse(Long regId, boolean allocated, String examName, String centerName, String city,
                                       String address, String roomNumber, Integer floor, Integer seatNumber,
                                       String reason, LocalDateTime decidedAt) {}
