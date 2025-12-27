package com.example.habittracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "habit_checkins",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_checkins_habit_date", columnNames = {"habit_id", "checkin_date"})
    },
    indexes = {
        @Index(name = "ix_checkins_habit_id", columnList = "habit_id"),
        @Index(name = "ix_checkins_date", columnList = "checkin_date")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class HabitCheckIn {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "habit_id", nullable = false, foreignKey = @ForeignKey(name = "fk_checkins_habit"))
  private Habit habit;

  @Column(name = "checkin_date", nullable = false)
  private LocalDate date;
}
