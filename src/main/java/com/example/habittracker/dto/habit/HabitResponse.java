package com.example.habittracker.dto.habit;

import java.time.Instant;

public record HabitResponse(
    Long id,
    String name,
    String description,
    boolean active,
    Instant createdAt,
    Instant updatedAt,
    HabitStatsResponse stats
) {}
