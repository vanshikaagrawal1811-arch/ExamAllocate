package com.examallocate.dto;

import jakarta.validation.constraints.*;

public final class AdminRequests {
    private AdminRequests() {}
    public record CenterRequest(@NotBlank String centerName, @NotBlank String city, String address, @Min(1) int totalCapacity) {}
    public record RoomRequest(@NotNull Long centerId, @NotBlank String roomNumber, @Min(1) int capacity, Integer floor) {}
    public record SessionRequest(@NotBlank String examName, @NotNull java.time.LocalDate examDate, @NotBlank String shiftName,
                                 java.time.LocalTime startTime, java.time.LocalTime endTime) {}
    public record StatusUpdate(@NotBlank String status, String resolution) {}
}
