package com.examallocate.dto;

public record AllocationRunResult(Long sessionId, int studentsProcessed, int successfulAllocations,
                                  int fallbacksTriggered, int unplaced, String status) {}
