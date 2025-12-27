package com.example.habittracker.dto.habit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HabitCreateRequest(
    @NotBlank @Size(max = 120) String name,
    @Size(max = 500) String description
) {}
