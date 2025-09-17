    package com.springboot.todolist.controllers;

    import com.springboot.todolist.models.Task;
    import com.springboot.todolist.models.User;
    import com.springboot.todolist.services.TaskService;
    import com.springboot.todolist.services.UserService;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                   Model model) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            User user = userService.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Page<Task> tasks = taskService.getTasksByUserAndStatusAndCreateAt(
                    user.getUser_id(), page, size, Task.TaskStatus.CURRENT);

            int numberOfCurrentTask = taskService.countTask(user, Task.TaskStatus.CURRENT);
            int numberOfFinishedTask = taskService.countTask(user, Task.TaskStatus.FINISHED);
            int totalTask = numberOfFinishedTask + numberOfCurrentTask;

            int safeTotalTask = (totalTask == 0) ? 1 : totalTask;

            String message;
            if (totalTask == 0) {
                message = "Let's start your first task 🚀";
            } else if (totalTask == numberOfFinishedTask) {
                message = "Well done 🎉";
            } else if ((double) numberOfFinishedTask / totalTask >= 0.7) {
                message = "Almost there 🔥";
            } else if ((double) numberOfFinishedTask / totalTask >= 0.4) {
                message = "Nice work ⭐";
            } else {
                message = "Keep going 💪";
            }


            model.addAttribute("user", user);
            model.addAttribute("role", user.getRole());
            model.addAttribute("tasks", tasks);
            model.addAttribute("totalTask", safeTotalTask);
            model.addAttribute("numberOfFinished", numberOfFinishedTask);
            model.addAttribute("message", message);
            model.addAttribute("newTask", new Task());

            return "home";
        }


        @PostMapping("/add-task")
        public String addTask(@ModelAttribute Task task, Model model) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            User user = userService.findByEmail(userEmail).orElse(null);

            task.setUser(user);

            model.addAttribute("openAddModal", true);
            model.addAttribute("newTask", task);
            model.addAttribute("user", user);
            taskService.addTask(task);

            return "redirect:/home";
        }


        // Cập nhật task
        @PostMapping("/update-task/{id}")
        public String updateTask(@PathVariable Long id, @ModelAttribute Task updatedTask) {
            taskService.updateTask(id, updatedTask);
            return "redirect:/home";
        }

        // Xóa task (Them task vao thung rac)
        @PostMapping("/delete-task/{id}")
        public String deleteTask(@PathVariable Long id) {
            Task task = taskService.getTaskById(id).orElseThrow();
            task.setStatus(Task.TaskStatus.REMOVED);
            taskService.updateTask(id, task);
            return "redirect:/home";
        }

        // Xóa task trong current (Them task vao thung rac)
        @PostMapping("/delete-task-current/{id}")
        public String deleteTaskCurrent(@PathVariable Long id,
                                        RedirectAttributes redirectAttributes) {
            try {
                Task task = taskService.getTaskById(id).orElseThrow();
                task.setStatus(Task.TaskStatus.REMOVED);
                taskService.updateTask(id, task);
                redirectAttributes.addFlashAttribute("success", "Remove successfully!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Remove error!");
            }
            return "redirect:/personal?tab=current";
        }

        // API để lấy chi tiết task (cho AJAX)
        @GetMapping("/api/task/{id}")
        @ResponseBody
        public Task getTaskById(@PathVariable Long id) {
            Optional<Task> task = taskService.getTaskById(id);
            return task.orElse(null);
        }


        @PostMapping("/tasks/{id}/status")
        @ResponseBody
        public String updateStatus(@PathVariable Long id,
                                   @RequestParam Task.TaskStatus status) {

            taskService.updateTaskStatus(id, status);
            return "ok";
        }

        // Restore task
        @PostMapping("/restore-task/{id}")
        public String restoreTask(@PathVariable long id, RedirectAttributes redirectAttributes) {

            try {
                Task task = taskService.getTaskById(id).orElseThrow();
                task.setStatus(Task.TaskStatus.CURRENT);
                taskService.updateTask(id, task);
                redirectAttributes.addFlashAttribute("success", "Restore successfully!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Restore failed!");
            }
            return "redirect:/personal?tab=removed";
        }

        // Delete Permanent task
        @PostMapping("/delete-task-permanent/{id}")
        public String deleteTaskPermanent(@PathVariable long id,
                                          @RequestParam String tab,
                                          @RequestParam String from,
                                          RedirectAttributes redirectAttributes) {
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