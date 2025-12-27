package com.example.habittracker.service;

import com.example.habittracker.config.JwtService;
import com.example.habittracker.dto.auth.AuthResponse;
import com.example.habittracker.dto.auth.LoginRequest;
import com.example.habittracker.dto.auth.RegisterRequest;
import com.example.habittracker.entity.User;
import com.example.habittracker.exception.ConflictException;
import com.example.habittracker.exception.UnauthorizedException;
import com.example.habittracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest req) {
    if (userRepository.existsByUsername(req.username())) {
      throw new ConflictException("Username already exists");
    }

    User user = User.builder()
        .username(req.username().trim())
        .passwordHash(passwordEncoder.encode(req.password()))
        .build();

    userRepository.save(user);

    String token = jwtService.generateToken(user.getUsername());
    return new AuthResponse(token, "Bearer", user.getUsername());
  }

  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest req) {
    User user = userRepository.findByUsername(req.username().trim())
        .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

    if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
      throw new UnauthorizedException("Invalid username or password");
    }

    String token = jwtService.generateToken(user.getUsername());
    return new AuthResponse(token, "Bearer", user.getUsername());
  }
}
