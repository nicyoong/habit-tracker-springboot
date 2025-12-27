package com.example.habittracker.dto.habit;

public record HabitStatsResponse(
    int currentStreak,
    int longestStreak,
    int totalCheckIns
) {}
