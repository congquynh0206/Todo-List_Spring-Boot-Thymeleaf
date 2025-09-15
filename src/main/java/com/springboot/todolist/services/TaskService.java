package com.springboot.todolist.services;

import com.springboot.todolist.models.Task;
import com.springboot.todolist.models.User;
import com.springboot.todolist.repository.TaskRepository;
import com.springboot.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    // Thêm task
    public Task addTask(Task newTask) {
        return taskRepository.save(newTask);
    }

    public int countTask(User user, Task.TaskStatus status) {
        return taskRepository.countByUserAndStatus(user, status);
    }

    public int countTaskByStatus(Task.TaskStatus status) {
        return taskRepository.countTasksByStatus(status);
    }

    public long countAllTask() {
        return taskRepository.count();
    }

    // Lấy tất cả task
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Lấy danh sách theo status
    public List<Task> getTaskByStatus(User user, Task.TaskStatus status) {
        return taskRepository.findByUserAndStatus(user, status);
    }

    // Lấy tasks theo user
    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    // Lấy 1 task theo id
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Update task
    public Optional<Task> updateTask(Long taskId, Task updatedTask) {
        return taskRepository.findById(taskId).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setDueDate(updatedTask.getDueDate());
            task.setStatus(updatedTask.getStatus());
            return taskRepository.save(task);
        });
    }

    public void updateTaskStatus(Long id, Task.TaskStatus status) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus(status);
        taskRepository.save(task);
    }

    // Xóa task
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public Page<Task> getTasksByUserAndStatusAndCreateAt(Long userId, int page, int size, Task.TaskStatus status) {
        User user = userRepository.findById(userId).orElseThrow();
        return taskRepository.findByUserAndStatusOrderByCreatedAtDesc(user, PageRequest.of(page, size), status);
    }
    public Page<Task> getTasksByUserAndStatus(Long userId, int page, int size, String status) {
        User user = userRepository.findById(userId).orElseThrow();
        Task.TaskStatus enumStatus = Task.TaskStatus.valueOf(status.toUpperCase());
        return taskRepository.findByUserAndStatus(user, PageRequest.of(page, size), enumStatus);
    }
    public Page<Task> getTasksByStatus(int page, int size, String status) {
        Task.TaskStatus enumStatus = Task.TaskStatus.valueOf(status.toUpperCase());
        return taskRepository.findByStatus(PageRequest.of(page, size), enumStatus);
    }
    public Page<Task> getAllTaskPage (int page, int size){
        return taskRepository.findAllBy(PageRequest.of(page,size));
    }
}
