package com.examallocate.dto;

import jakarta.validation.constraints.*;

public record GrievanceRequest(@NotNull Long regId, @NotBlank String applicationNo, @NotBlank @Size(max = 1000) String description) {}
