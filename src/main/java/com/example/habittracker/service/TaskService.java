package com.example.habittracker.service;

import com.example.habittracker.dto.task.TaskCreateRequest;
import com.example.habittracker.dto.task.TaskResponse;
import com.example.habittracker.dto.task.TaskUpdateRequest;
import com.example.habittracker.entity.Task;
import com.example.habittracker.entity.User;
import com.example.habittracker.exception.ResourceNotFoundException;
import com.example.habittracker.repository.TaskRepository;
import com.example.habittracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;

  public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<TaskResponse> list(String username) {
    Long userId = getUserId(username);
    return taskRepository.findAllByUserIdOrderByUpdatedAtDesc(userId).stream().map(this::toDto).toList();
  }

  @Transactional
  public TaskResponse create(String username, TaskCreateRequest req) {
    User user = getUser(username);

    Task task = Task.builder()
        .user(user)
        .title(req.title().trim())
        .notes(req.notes())
        .done(false)
        .build();

    taskRepository.save(task);
    return toDto(task);
  }

  @Transactional
  public TaskResponse update(String username, Long taskId, TaskUpdateRequest req) {
    Long userId = getUserId(username);
    Task task = taskRepository.findByIdAndUserId(taskId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    if (req.title() != null) task.setTitle(req.title().trim());
    if (req.notes() != null) task.setNotes(req.notes());
    if (req.done() != null) task.setDone(req.done());

    return toDto(task);
  }

  @Transactional
  public void delete(String username, Long taskId) {
    Long userId = getUserId(username);
    Task task = taskRepository.findByIdAndUserId(taskId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    taskRepository.delete(task);
  }

  private TaskResponse toDto(Task t) {
    return new TaskResponse(t.getId(), t.getTitle(), t.getNotes(), t.isDone(), t.getCreatedAt(), t.getUpdatedAt());
  }

  private User getUser(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  private Long getUserId(String username) {
    return getUser(username).getId();
  }
}
