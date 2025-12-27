package com.example.habittracker.repository;

import com.example.habittracker.entity.HabitCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitCheckInRepository extends JpaRepository<HabitCheckIn, Long> {
  Optional<HabitCheckIn> findByHabitIdAndDate(Long habitId, LocalDate date);
  List<HabitCheckIn> findAllByHabitIdOrderByDateAsc(Long habitId);
  long countByHabitId(Long habitId);
}
