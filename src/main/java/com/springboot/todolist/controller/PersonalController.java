package com.springboot.todolist.controller;

import com.springboot.todolist.model.Task;
import com.springboot.todolist.model.User;
import com.springboot.todolist.service.TaskService;
import com.springboot.todolist.service.UserService;
import java.nio.file.Path;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class PersonalController {

    @Autowired
    private UserService userService;
    @Autowired
    private  TaskService taskService;

    @GetMapping("/personal")
    public String showPersonalPage(@RequestParam(defaultValue = "current") String tab,
                                   @RequestParam (defaultValue = "0") int page,
                                   @RequestParam (defaultValue = "5") int size,
                                   HttpSession session,
                                   Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(userEmail).orElseThrow();
        model.addAttribute("user", user);

        // Xác định tab active
        model.addAttribute("activeTab", tab);

        switch (tab) {
            case "finished":
                model.addAttribute("listFinished", taskService.getTasksByUserAndStatus(user.getUser_id(),page,size, Task.TaskStatus.FINISHED));
                break;
            case "removed":
                model.addAttribute("listRemoved", taskService.getTasksByUserAndStatus(user.getUser_id(),page,size, Task.TaskStatus.REMOVED));
                break;
            case "current":
                model.addAttribute("listCurrent", taskService.getTasksByUserAndStatus(user.getUser_id(),page,size, Task.TaskStatus.CURRENT));
                break;
            default:
                model.addAttribute("user",user);
                break;
        }
        return "personal";
    }


    // Lấy Current Task
    @GetMapping("/tasks/current")
    public String getCurrentTask ( @RequestParam (defaultValue = "0") int page,
                                   @RequestParam (defaultValue = "5") int size,
                                   HttpSession session, Model model){
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(userEmail).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("listCurrent", taskService.getTasksByUserAndStatus(user.getUser_id(),page,size, Task.TaskStatus.CURRENT));
        model.addAttribute("activeTab","current");
        return "personal";
    }

    // Lấy Finished Task
    @GetMapping("/tasks/finished")
    public String getFinishedTask ( @RequestParam (defaultValue = "0") int page,
                                    @RequestParam (defaultValue = "5") int size,
                                    HttpSession session, Model model){
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(userEmail).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("user",user);
        model.addAttribute("listFinished", taskService.getTasksByUserAndStatus(user.getUser_id(),page,size, Task.TaskStatus.FINISHED));
        model.addAttribute("activeTab","finished");
        return "personal";
    }

    // Lấy Removed Task
    @GetMapping("/tasks/removed")
    public String getRemovedTask ( @RequestParam (defaultValue = "0") int page,
                                   @RequestParam (defaultValue = "5") int size,
                                   HttpSession session, Model model){
        String email = (String) session.getAttribute("userEmail");
        if(email == null){
            return "redirect:/login";
        }
        User user = userService.findByEmail(email).orElseThrow();
        model.addAttribute("user",user);
        model.addAttribute("listRemoved", taskService.getTasksByUserAndStatus(user.getUser_id(),page,size, Task.TaskStatus.FINISHED));
        model.addAttribute("activeTab","removed");
        return "personal";
    }

    // Change Infor
    @GetMapping("/tasks/infor")
    public String getUserToChange ( HttpSession session, Model model){
        String email = (String) session.getAttribute("userEmail");
        if(email == null){
            return "redirect:/login";
        }
        User user = userService.findByEmail(email).orElseThrow();
        model.addAttribute("user",user);
        model.addAttribute("activeTab","infor");
        return "personal";
    }



    // Xử lý cập nhật username
    @PostMapping("/update-username")
    public String updateUsername(@RequestParam String newName,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail == null) {
            return "redirect:/login";
        }

        try {
            userService.updateDisplayName(userEmail, newName);
            redirectAttributes.addFlashAttribute("success_username", "Name updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_username", "Name updated fail!");
        }

        return "redirect:/personal?tab=infor";
    }

    // Xử lý cập nhật email
    @PostMapping("/update-email")
    public String updateEmail(@RequestParam String newEmail,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail == null) {
            return "redirect:/login";
        }

        // Kiểm tra email mới đã tồn tại chưa
        if (userService.findByEmail(newEmail).isPresent() && !newEmail.equals(userEmail)) {
            redirectAttributes.addFlashAttribute("error_email", "This email is existed!");
            return "redirect:/personal?tab=infor";
        }

        try {
            userService.updateEmail(userEmail, newEmail);
            // Cập nhật session với email mới
            session.setAttribute("userEmail", newEmail);
            redirectAttributes.addFlashAttribute("success_email", "Email updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_email", "Email updated fail!");
        }

        return "redirect:/personal?tab=infor";
    }

    // Xử lý cập nhật password
    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByEmail(userEmail);
        if (user.isPresent()) {
            // Kiểm tra mật khẩu hiện tại
            if (!user.get().getPassword().equals(currentPassword)) {
                redirectAttributes.addFlashAttribute("error_password", "Current password is incorrect!");
                return "redirect:/personal?tab=infor";
            }

            try {
                userService.updatePassword(userEmail, newPassword);
                redirectAttributes.addFlashAttribute("success_password", "Password updated successfully!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error_password", "Password updated fail!");
            }
        }
        return "redirect:/personal?tab=infor";
    }

    // Upload Avatar
    @PostMapping("/update-avatar")
    public String updateAvatar(@RequestParam("avatarFile") MultipartFile file,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }

        // Kiểm tra file có rỗng không
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error_avatar", "Vui lòng chọn file!");
            return "redirect:/personal";
        }

        // Kiểm tra loại file (chỉ cho phép PNG, JPG, JPEG, GIF, WEBP)
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/gif") ||
                        contentType.equals("image/webp"))) {
            redirectAttributes.addFlashAttribute("error_avatar", "Chỉ chấp nhận ảnh PNG, JPG, GIF, WEBP!");
            return "redirect:/personal";
        }

        // Giới hạn dung lượng (VD: tối đa 2MB)
        long maxFileSize = 2 * 1024 * 1024; // 2MB
        if (file.getSize() > maxFileSize) {
            redirectAttributes.addFlashAttribute("error_avatar", "Kích thước file tối đa 2MB!");
            return "redirect:/personal";
        }

        try {
            // Thư mục lưu file (trong static/uploads)
            String uploadDir = "uploads/";

            // Tạo thư mục nếu chưa tồn tại
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Tạo tên file duy nhất (timestamp + tên gốc)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Lưu file vào server
            Path path = Paths.get(uploadDir + fileName);
            Files.write(path, file.getBytes());

            // Cập nhật avatar trong DB
            User user = userService.findByEmail(userEmail).orElseThrow();
            user.setAvatar(fileName); // đường dẫn để Thymeleaf load
            userService.save(user);

            redirectAttributes.addFlashAttribute("success_avatar", "Cập nhật avatar thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error_avatar", "Lỗi khi upload ảnh!");
        }

        return "redirect:/personal";
    }

    @PostMapping("/reset-avatar")
    public String resetAvatar (HttpSession session){
        String email = (String) session.getAttribute("userEmail");
        if (email == null){
            return "redirect:login";
        }
        User user = userService.findByEmail(email).orElseThrow();
        user.setAvatar("default-avatar.png");
        userService.save(user);
        return "redirect:/personal";
    }


}
