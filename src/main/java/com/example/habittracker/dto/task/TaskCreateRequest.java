package com.example.habittracker.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskCreateRequest(
    @NotBlank @Size(max = 120) String title,
    @Size(max = 1000) String notes
) {}
