package com.example.habittracker.repository;

import com.example.habittracker.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
  List<Habit> findAllByUserIdOrderByUpdatedAtDesc(Long userId);
  Optional<Habit> findByIdAndUserId(Long id, Long userId);
}
