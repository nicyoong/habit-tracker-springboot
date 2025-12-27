package com.example.habittracker.dto.habit;

import jakarta.validation.constraints.Size;

public record HabitUpdateRequest(
    @Size(max = 120) String name,
    @Size(max = 500) String description,
    Boolean active
) {}
