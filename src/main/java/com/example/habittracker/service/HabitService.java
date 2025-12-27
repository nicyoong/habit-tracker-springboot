package com.example.habittracker.service;

import com.example.habittracker.dto.habit.*;
import com.example.habittracker.entity.Habit;
import com.example.habittracker.entity.HabitCheckIn;
import com.example.habittracker.entity.User;
import com.example.habittracker.exception.ConflictException;
import com.example.habittracker.exception.ResourceNotFoundException;
import com.example.habittracker.repository.HabitCheckInRepository;
import com.example.habittracker.repository.HabitRepository;
import com.example.habittracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
public class HabitService {

  private final HabitRepository habitRepository;
  private final HabitCheckInRepository checkInRepository;
  private final UserRepository userRepository;

  public HabitService(HabitRepository habitRepository, HabitCheckInRepository checkInRepository, UserRepository userRepository) {
    this.habitRepository = habitRepository;
    this.checkInRepository = checkInRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<HabitResponse> list(String username) {
    Long userId = getUserId(username);
    return habitRepository.findAllByUserIdOrderByUpdatedAtDesc(userId).stream()
        .map(h -> toDto(h, computeStats(h.getId())))
        .toList();
  }

  @Transactional
  public HabitResponse create(String username, HabitCreateRequest req) {
    User user = getUser(username);

    Habit habit = Habit.builder()
        .user(user)
        .name(req.name().trim())
        .description(req.description())
        .active(true)
        .build();

    habitRepository.save(habit);
    return toDto(habit, new HabitStatsResponse(0, 0, 0));
  }

  @Transactional
  public HabitResponse update(String username, Long habitId, HabitUpdateRequest req) {
    Habit habit = getHabitForUser(username, habitId);

    if (req.name() != null) habit.setName(req.name().trim());
    if (req.description() != null) habit.setDescription(req.description());
    if (req.active() != null) habit.setActive(req.active());

    return toDto(habit, computeStats(habitId));
  }

  @Transactional
  public void delete(String username, Long habitId) {
    Habit habit = getHabitForUser(username, habitId);
    habitRepository.delete(habit);
  }

  @Transactional
  public HabitResponse checkIn(String username, Long habitId, HabitCheckInRequest req) {
    Habit habit = getHabitForUser(username, habitId);
    LocalDate date = req.date();

    checkInRepository.findByHabitIdAndDate(habit.getId(), date)
        .ifPresent(existing -> { throw new ConflictException("Already checked in for this date"); });

    HabitCheckIn checkIn = HabitCheckIn.builder()
        .habit(habit)
        .date(date)
        .build();

    checkInRepository.save(checkIn);
    return toDto(habit, computeStats(habitId));
  }

  @Transactional
  public HabitResponse undoCheckIn(String username, Long habitId, LocalDate date) {
    Habit habit = getHabitForUser(username, habitId);

    HabitCheckIn checkIn = checkInRepository.findByHabitIdAndDate(habit.getId(), date)
        .orElseThrow(() -> new ResourceNotFoundException("Check-in not found for that date"));

    checkInRepository.delete(checkIn);
    return toDto(habit, computeStats(habitId));
  }

  @Transactional(readOnly = true)
  public HabitStatsResponse stats(String username, Long habitId) {
    getHabitForUser(username, habitId); // authz check
    return computeStats(habitId);
  }

  private HabitStatsResponse computeStats(Long habitId) {
    List<HabitCheckIn> checkIns = checkInRepository.findAllByHabitIdOrderByDateAsc(habitId);
    int total = checkIns.size();
    if (total == 0) return new HabitStatsResponse(0, 0, 0);

    var dates = new HashSet<LocalDate>();
    for (var c : checkIns) dates.add(c.getDate());

    int longest = 0;
    int current = 0;

    // longest streak: scan all dates sorted
    LocalDate prev = null;
    int running = 0;
    for (var c : checkIns) {
      LocalDate d = c.getDate();
      if (prev == null) {
        running = 1;
      } else {
        long diff = prev.until(d).getDays();
        running = (diff == 1) ? (running + 1) : 1;
      }
      longest = Math.max(longest, running);
      prev = d;
    }

    // current streak: count backwards from today
    LocalDate today = LocalDate.now();
    LocalDate cursor = today;
    while (dates.contains(cursor)) {
      current++;
      cursor = cursor.minusDays(1);
    }

    return new HabitStatsResponse(current, longest, total);
  }

  private HabitResponse toDto(Habit h, HabitStatsResponse stats) {
    return new HabitResponse(h.getId(), h.getName(), h.getDescription(), h.isActive(), h.getCreatedAt(), h.getUpdatedAt(), stats);
  }

  private Habit getHabitForUser(String username, Long habitId) {
    Long userId = getUserId(username);
    return habitRepository.findByIdAndUserId(habitId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Habit not found"));
  }

  private User getUser(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  private Long getUserId(String username) {
    return getUser(username).getId();
  }
}
