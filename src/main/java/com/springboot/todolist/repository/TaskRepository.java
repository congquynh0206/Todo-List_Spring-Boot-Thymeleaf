package com.springboot.todolist.repository;

import com.springboot.todolist.models.Task;
import com.springboot.todolist.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserOrderByCreatedAtDesc(User user);
    List<Task> findByUserAndStatus(User user, Task.TaskStatus status);
    int countByUserAndStatus(User user, Task.TaskStatus status);
    int countTasksByStatus(Task.TaskStatus status);
    Page<Task> findByUserAndStatusOrderByCreatedAtDesc(User user, Pageable pageable, Task.TaskStatus status);
    Page<Task> findByUserAndStatus(User user, Pageable pageable, Task.TaskStatus status);
    Page<Task> findByStatus( Pageable pageable, Task.TaskStatus status);
    Page<Task> findAllBy(Pageable pageable);
}
