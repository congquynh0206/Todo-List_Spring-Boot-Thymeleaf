package com.springboot.todolist.controller;

import com.springboot.todolist.model.User;
import com.springboot.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignUpPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user,
                                RedirectAttributes redirectAttributes) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("emailError", "Email is existed!");
            return "redirect:/signup";
        }
        try {
            user.setAvatar("/icon/default-avatar.png");
            userService.save(user);
            return "redirect:/signup?success=true";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while registering!");
            return "redirect:/signup";
        }
    }

    @GetMapping("/login")
    public String showLoginPage (){
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        return userService.findByEmail(email)
                .filter(user -> user.getPassword().equals(password))
                .map(user -> {
                    // Lưu thông tin user vào session
                    session.setAttribute("userEmail", user.getEmail());
                    session.setAttribute("userId", user.getUser_id());
                    return "redirect:/home";
                }).orElseGet(() -> {
                    // Thêm thông báo lỗi khi đăng nhập thất bại
                    redirectAttributes.addFlashAttribute("error", "Email or password is incorrecrt!");
                    return "redirect:/login";
                });
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}