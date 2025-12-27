package com.example.habittracker.controller;

import com.example.habittracker.dto.habit.*;
import com.example.habittracker.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

  private final HabitService habitService;

  public HabitController(HabitService habitService) {
    this.habitService = habitService;
  }

  @GetMapping
  public List<HabitResponse> list(Authentication auth) {
    return habitService.list(auth.getName());
  }

  @PostMapping
  public HabitResponse create(Authentication auth, @Valid @RequestBody HabitCreateRequest req) {
    return habitService.create(auth.getName(), req);
  }

  @PatchMapping("/{id}")
  public HabitResponse update(Authentication auth, @PathVariable Long id, @Valid @RequestBody HabitUpdateRequest req) {
    return habitService.update(auth.getName(), id, req);
  }

  @DeleteMapping("/{id}")
  public void delete(Authentication auth, @PathVariable Long id) {
    habitService.delete(auth.getName(), id);
  }

  @GetMapping("/{id}/stats")
  public HabitStatsResponse stats(Authentication auth, @PathVariable Long id) {
    return habitService.stats(auth.getName(), id);
  }

  @PostMapping("/{id}/checkins")
  public HabitResponse checkIn(Authentication auth, @PathVariable Long id, @Valid @RequestBody HabitCheckInRequest req) {
    return habitService.checkIn(auth.getName(), id, req);
  }

  @DeleteMapping("/{id}/checkins/{date}")
  public HabitResponse undoCheckIn(Authentication auth, @PathVariable Long id, @PathVariable LocalDate date) {
    return habitService.undoCheckIn(auth.getName(), id, date);
  }
}
