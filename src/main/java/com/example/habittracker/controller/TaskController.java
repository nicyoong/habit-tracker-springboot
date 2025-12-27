package com.example.habittracker.controller;

import com.example.habittracker.dto.task.TaskCreateRequest;
import com.example.habittracker.dto.task.TaskResponse;
import com.example.habittracker.dto.task.TaskUpdateRequest;
import com.example.habittracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping
  public List<TaskResponse> list(Authentication auth) {
    return taskService.list(auth.getName());
  }

  @PostMapping
  public TaskResponse create(Authentication auth, @Valid @RequestBody TaskCreateRequest req) {
    return taskService.create(auth.getName(), req);
  }

  @PatchMapping("/{id}")
  public TaskResponse update(Authentication auth, @PathVariable Long id, @Valid @RequestBody TaskUpdateRequest req) {
    return taskService.update(auth.getName(), id, req);
  }

  @DeleteMapping("/{id}")
  public void delete(Authentication auth, @PathVariable Long id) {
    taskService.delete(auth.getName(), id);
  }
}
