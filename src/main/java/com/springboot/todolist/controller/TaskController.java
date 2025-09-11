package com.springboot.todolist.controller;

import com.springboot.todolist.model.Task;
import com.springboot.todolist.model.User;
import com.springboot.todolist.service.TaskService;
import com.springboot.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    // Hiển thị trang home với danh sách tasks
    @GetMapping("/home")
    public String showHomePage(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "4") int size,
                               HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        Page<Task> tasks = taskService.getTasksByUserAndStatus(user.getUser_id(), page, size, Task.TaskStatus.CURRENT);

        int numberOfCurrentTask = taskService.countTask(user, Task.TaskStatus.CURRENT);
        int numberOfFinishedTask = taskService.countTask(user, Task.TaskStatus.FINISHED);
        int totalTask = numberOfFinishedTask + numberOfCurrentTask;

        String message;
        if (totalTask == numberOfFinishedTask) {
            message = "Well done 🎉";
        } else if ((double) numberOfFinishedTask / totalTask >= 0.7) {
            message = "Almost there 🔥";
        } else if ((double) numberOfFinishedTask / totalTask >= 0.4) {
            message = "Nice work ⭐";
        } else {
            message = "Keep going 💪";
        }

        model.addAttribute("user", user);
        model.addAttribute("tasks", tasks);
        model.addAttribute("totalTask", totalTask);
        model.addAttribute("numberOfFinished", numberOfFinishedTask);
        model.addAttribute("message", message);
        model.addAttribute("newTask", new Task());

        return "home";
    }


    @PostMapping("/add-task")
    public String addTask(@ModelAttribute Task task, HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(userEmail).orElse(null);

        task.setUser(user);

        if (task.getTitle() == null || task.getTitle().isBlank()
                || task.getDescription() == null || task.getDescription().isBlank()
                || task.getDueDate() == null) {
            model.addAttribute("error", "Any field cannot be empty!");
            model.addAttribute("openAddModal", true);
            model.addAttribute("newTask", task);
            model.addAttribute("user", user);
            return "home";
        }
        taskService.addTask(task);

        return "redirect:/home";
    }


    // Cập nhật task
    @PostMapping("/update-task/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task updatedTask, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }

        taskService.updateTask(id, updatedTask);
        return "redirect:/home";
    }

    // Xóa task (Them task vao thung rac)
    @PostMapping("/delete-task/{id}")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }
        Task task = taskService.getTaskById(id).orElseThrow();
        task.setStatus(Task.TaskStatus.REMOVED);
        taskService.updateTask(id,task);
        return "redirect:/home";
    }

    // Xóa task trong current (Them task vao thung rac)
    @PostMapping("/delete-task-current/{id}")
    public String deleteTaskCurrent(@PathVariable Long id, HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }
        try {
            Task task = taskService.getTaskById(id).orElseThrow();
            task.setStatus(Task.TaskStatus.REMOVED);
            taskService.updateTask(id,task);
            redirectAttributes.addFlashAttribute("success", "Remove successfully!");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Remove error!");
        }
        return "redirect:/personal?tab=current";
    }

    // API để lấy chi tiết task (cho AJAX)
    @GetMapping("/api/task/{id}")
    @ResponseBody
    public Task getTaskById(@PathVariable Long id, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return null;
        }
        Optional<Task> task = taskService.getTaskById(id);
        return task.orElse(null);
    }



    @PostMapping("/tasks/{id}/status")
    @ResponseBody
    public String updateStatus(@PathVariable Long id,
                               @RequestParam Task.TaskStatus status,
                               HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }

        taskService.updateTaskStatus(id, status);
        return "ok";
    }

    // Restore task
    @PostMapping("/restore-task/{id}")
    public String restoreTask (@PathVariable long id, HttpSession session, RedirectAttributes redirectAttributes){
        String email = (String) session.getAttribute("userEmail");
        if (email == null){
            return "redirect:/login";
        }
        try {
            Task task = taskService.getTaskById(id).orElseThrow();
            task.setStatus(Task.TaskStatus.CURRENT);
            taskService.updateTask(id,task);
            redirectAttributes.addFlashAttribute("success", "Restore successfully!");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Restore failed!");
        }
        return "redirect:/personal?tab=removed";
    }

    // Delete Permanent task
    @PostMapping("/delete-task-permanent/{id}")
    public String deleteTaskPermanent(@PathVariable long id,
                                      @RequestParam String tab,
                                      @RequestParam String from,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) {
            return "redirect:/login";
        }
        try {
            taskService.deleteTask(id);
            redirectAttributes.addFlashAttribute("success", "Remove successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Remove failed!");
        }

        if ("admin".equals(from)) {
            return "redirect:/admin?tab=" + tab;
        } else {
            return "redirect:/personal?tab=" + tab;
        }
    }



}