package com.example.habittracker.dto.habit;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record HabitCheckInRequest(
    @NotNull LocalDate date
) {}
