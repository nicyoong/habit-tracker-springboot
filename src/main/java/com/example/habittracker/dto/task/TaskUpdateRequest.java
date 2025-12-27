package com.example.habittracker.dto.task;

import jakarta.validation.constraints.Size;

public record TaskUpdateRequest(
    @Size(max = 120) String title,
    @Size(max = 1000) String notes,
    Boolean done
) {}
