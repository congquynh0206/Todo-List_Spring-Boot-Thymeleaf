package com.springboot.todolist.controllers;

import com.springboot.todolist.models.Task;
import com.springboot.todolist.models.User;
import com.springboot.todolist.services.TaskService;
import com.springboot.todolist.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private  TaskService taskService;

    @GetMapping("/admin")
    public String showAdminPage(@RequestParam(defaultValue = "dashboard") String tab,
                                   @RequestParam (defaultValue = "0") int page,
                                   @RequestParam (defaultValue = "5") int size,
                                   @RequestParam(required = false) String status,
                                   Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findByEmail(userEmail).orElseThrow();
        model.addAttribute("user", user);

        // Xác định tab active
        model.addAttribute("activeTab", tab);
        Page<Task> listTask;
        if (status == null || status.isEmpty() || status.equals("ALL")){
            listTask = taskService.getAllTaskPage(page, size);
        }else {
            listTask = taskService.getTasksByStatus (page, size, status);
        }

        switch (tab) {
            case "dashboard":
                break;
            case "user":
                model.addAttribute("listUser", userService.findByRole("USER", page,size));
                model.addAttribute("listAdmin", userService.findByRole("ADMIN", page,size));
                break;
            case "task":
                model.addAttribute("listTask", listTask);
                model.addAttribute("status", status);
                break;
            default:
                model.addAttribute("user",user);
                break;
        }
        return "admin";
    }



    @GetMapping("/admin/user")
    public String userManagement (@RequestParam (defaultValue = "0") int pageUser,
                                  @RequestParam (defaultValue = "5") int sizeUser,
                                  @RequestParam (defaultValue = "0") int pageAdmin,
                                  @RequestParam (defaultValue = "5") int sizeAdmin,
                                  Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findByEmail(userEmail).orElseThrow();
        model.addAttribute("listUser", userService.findByRole("USER", pageUser,sizeUser));
        model.addAttribute("listAdmin", userService.findByRole("ADMIN", pageAdmin,sizeAdmin));
        model.addAttribute("user", user);
        model.addAttribute("activeTab", "user");
        model.addAttribute("sizeAdmin", sizeAdmin);
        model.addAttribute("sizeUser", sizeUser);
        return "admin";
    }
    @GetMapping("/admin/task")
    public String taskManagement (@RequestParam (defaultValue = "0") int page,
                                  @RequestParam (defaultValue = "5") int size,
                                  @RequestParam (required = false ) String status,
                                  HttpSession session,
                                  Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findByEmail(userEmail).orElseThrow();
        Page<Task> listTask;
        if (status == null || status.isEmpty() || status.equals("ALL")){
            listTask = taskService.getAllTaskPage(page, size);
        }else {
            listTask = taskService.getTasksByStatus (page, size, status);
        }
        model.addAttribute("listTask", listTask);
        model.addAttribute("user", user);
        model.addAttribute("activeTab", "task");
        model.addAttribute("status", status);
        model.addAttribute("size", size);
        return "admin";
    }

    // Xử lý cập nhật password
    @PostMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable long id,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findById(id).orElseThrow();
            try {
                userService.resetPassword(id, "123456");
                redirectAttributes.addFlashAttribute("success", "Password reset successfully for " + user.getDisplayName());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Password reset fail!");
            }
        return "redirect:/admin?tab=user";
    }

    // Set Admin
    @PostMapping("/set-admin/{id}")
    public String setAdmin (@PathVariable long id, RedirectAttributes redirectAttributes){

        try {
            User user = userService.findById(id).orElseThrow();
            userService.setRole(id,"ADMIN" );
            redirectAttributes.addFlashAttribute("success", "Set Admin Successfully for " + user.getDisplayName());
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Set Admin Failed!");
        }
        return "redirect:/admin?tab=user";
    }

    // Xóa user
    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable long id, RedirectAttributes redirectAttributes){

        try {
            User user = userService.findById(id).orElseThrow();
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Delete successfully account name: " + user.getDisplayName());
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Delete failed!");
        }
        return "redirect:/admin?tab=user";
    }

    @GetMapping("/api/chart")
    @ResponseBody
    public Map<String, Object> getChartData(){
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> allTask = new HashMap<>();
        int numOfCurrentTask = taskService.countTaskByStatus(Task.TaskStatus.CURRENT);
        int numOfFinishedTask = taskService.countTaskByStatus(Task.TaskStatus.FINISHED);
        int numOfRemovedTask = taskService.countTaskByStatus(Task.TaskStatus.REMOVED);
        long totalTask = taskService.countAllTask();
        allTask.put("labels", List.of("Current","Finished","Removed","Total"));
        allTask.put("values", List.of(numOfCurrentTask,numOfFinishedTask,numOfRemovedTask,totalTask));

        Map<String, Double> completionRate = new HashMap<>();
        double rate = (double) numOfFinishedTask * 100 / totalTask ;
        completionRate.put("completionRate", rate);

        response.put("allTask",allTask);
        response.put("completionRate",rate);

         return response;
    }

}
