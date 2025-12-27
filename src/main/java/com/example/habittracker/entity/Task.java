package com.example.habittracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tasks",
    indexes = {
        @Index(name = "ix_tasks_user_id", columnList = "user_id"),
        @Index(name = "ix_tasks_done", columnList = "done")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tasks_user"))
  private User user;

  @Column(nullable = false, length = 120)
  private String title;

  @Column(length = 1000)
  private String notes;

  @Column(nullable = false)
  private boolean done;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  void onCreate() {
    var now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }
}
