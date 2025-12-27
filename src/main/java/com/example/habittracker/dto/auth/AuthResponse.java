package com.example.habittracker.dto.auth;

public record AuthResponse(
    String token,
    String tokenType,
    String username
) {}
