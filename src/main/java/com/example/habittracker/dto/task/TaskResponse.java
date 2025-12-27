package com.example.habittracker.dto.task;

import java.time.Instant;

public record TaskResponse(
    Long id,
    String title,
    String notes,
    boolean done,
    Instant createdAt,
    Instant updatedAt
) {}
