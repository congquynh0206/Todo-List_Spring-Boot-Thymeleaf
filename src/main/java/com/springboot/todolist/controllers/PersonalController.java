package com.springboot.todolist.controllers;

import com.springboot.todolist.models.Task;
import com.springboot.todolist.models.User;
import com.springboot.todolist.services.TaskService;
import com.springboot.todolist.services.UserService;
import java.nio.file.Path;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class PersonalController {

    @Autowired
    private UserService userService;
    @Autowired
    private  TaskService taskService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/personal")
    public String showPersonalPage(@RequestParam(defaultValue = "current") String tab,
                                   @RequestParam (defaultValue = "0") int page,
                                   @RequestParam (defaultValue = "5") int size,
                                   Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findByEmail(userEmail).orElseThrow();
        model.addAttribute("user", user);

        // Xác định tab active
        model.addAttribute("activeTab", tab);

        switch (tab) {
            case "finished":
                model.addAttribute("listFinished", taskService.getTasksByUserAndStatus(user.getUser_id(),page,size, "FINISHED"));
                break;
            case "removed":
                model.addAttribute("listRemoved", taskService.getTasksByUserAndStatus(user.getUser_id(),page,size,"REMOVED"));
                break;
            case "current":
                model.addAttribute("listCurrent", taskService.getTasksByUserAndStatus(user.getUser_id(),page,size, "CURRENT"));
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
                                   @RequestParam (defaultValue = "0") int pageC,
                                   @RequestParam (defaultValue = "5") int sizeC,
                                   @RequestParam (defaultValue = "") String textFind,
                                    Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findByEmail(userEmail).orElseThrow();
        Page<Task> listCurrent;
        if (textFind != null){
            listCurrent = taskService.getTasksByUserAndStatusAndTitle(user.getUser_id(), pageC, sizeC, "CURRENT", textFind);
        }else{
            listCurrent = taskService.getTasksByUserAndStatus(user.getUser_id(),page,sizeC, "CURRENT");
        }

        model.addAttribute("user", user);
        model.addAttribute("listCurrent", listCurrent);
        model.addAttribute("activeTab","current");
        model.addAttribute("textFind",textFind);
        model.addAttribute("sizeC",sizeC);
        return "personal";
    }

    // Lấy Finished Task
    @GetMapping("/tasks/finished")
    public String getFinishedTask ( @RequestParam (defaultValue = "0") int page,
                                    @RequestParam (defaultValue = "0") int pageF,
                                    @RequestParam (defaultValue = "5") int sizeF,
                                    @RequestParam (defaultValue = "") String textFindF,
                                    Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findByEmail(userEmail).orElseThrow();

        Page<Task> listFinished;
        if (textFindF != null){
            listFinished = taskService.getTasksByUserAndStatusAndTitle(user.getUser_id(), pageF, sizeF, "FINISHED", textFindF);
        }else{
            listFinished = taskService.getTasksByUserAndStatus(user.getUser_id(),page,sizeF, "FINISHED");
        }

        model.addAttribute("user", user);
        model.addAttribute("user",user);
        model.addAttribute("listFinished", listFinished);
        model.addAttribute("textFindF", textFindF);
        model.addAttribute("pageF", pageF);
        model.addAttribute("sizeF", sizeF);
        model.addAttribute("activeTab","finished");
        return "personal";
    }

    // Lấy Removed Task
    @GetMapping("/tasks/removed")
    public String getRemovedTask ( @RequestParam (defaultValue = "0") int page,
                                   @RequestParam (defaultValue = "0") int pageR,
                                   @RequestParam (defaultValue = "5") int sizeR,
                                   @RequestParam (defaultValue = "") String textFindR,
                                   Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findByEmail(userEmail).orElseThrow();

        Page<Task> listRemoved;
        if (textFindR != null){
            listRemoved = taskService.getTasksByUserAndStatusAndTitle(user.getUser_id(), pageR, sizeR, "REMOVED", textFindR);
        }else{
            listRemoved = taskService.getTasksByUserAndStatus(user.getUser_id(),page,sizeR, "REMOVED");
        }


        model.addAttribute("user",user);
        model.addAttribute("listRemoved", listRemoved);
        model.addAttribute("textFindR", textFindR);
        model.addAttribute("pageR", pageR);
        model.addAttribute("sizeR", sizeR);
        model.addAttribute("activeTab","removed");
        return "personal";
    }

    // Change Infor
    @GetMapping("/tasks/infor")
    public String getUserToChange ( Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findByEmail(userEmail).orElseThrow();
        model.addAttribute("user",user);
        model.addAttribute("activeTab","infor");
        return "personal";
    }



    // Xử lý cập nhật username
    @PostMapping("/update-username")
    public String updateUsername(@RequestParam String newName,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

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
                              RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        try {
            if (!userService.findByEmail(newEmail).isEmpty()) {
                redirectAttributes.addFlashAttribute("error_email", "This email is existed!");
                return "redirect:/personal?tab=infor";
            }
            userService.updateEmail(userEmail, newEmail);
            redirectAttributes.addFlashAttribute("success_email", "Email updated successfully!");

            UserDetails userDetails = userDetailsService.loadUserByUsername(newEmail);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);

        } catch (Exception e) {
            e.printStackTrace(); // log lỗi để xem chi tiết
            redirectAttributes.addFlashAttribute("error_email", "Email updated fail!");
        }

        return "redirect:/personal?tab=infor";
    }

    // Xử lý cập nhật password
    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Optional<User> user = userService.findByEmail(userEmail);
        if (user.isPresent()) {
            // Kiểm tra mật khẩu hiện tại
            if (passwordEncoder.matches(user.get().getPassword(), currentPassword)) {
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Kiểm tra file có rỗng không
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error_avatar", "Please choose a file!");
            return "redirect:/personal";
        }

        // Kiểm tra loại file (chỉ cho phép PNG, JPG, JPEG, GIF, WEBP)
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/gif") ||
                        contentType.equals("image/webp"))) {
            redirectAttributes.addFlashAttribute("error", "Only accept image PNG, JPG, GIF, WEBP!");
            return "redirect:/personal";
        }

        // Giới hạn dung lượng (VD: tối đa 2MB)
        long maxFileSize = 2 * 1024 * 1024; // 2MB
        if (file.getSize() > maxFileSize) {
            redirectAttributes.addFlashAttribute("error", "Maximum file size 2MB!");
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
            user.setAvatar(fileName);
            userService.save(user);

            redirectAttributes.addFlashAttribute("success", "Avatar update successful!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error uploading photo!");
        }

        return "redirect:/personal";
    }

    @PostMapping("/reset-avatar")
    public String resetAvatar (){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findByEmail(userEmail).orElseThrow();
        user.setAvatar("default-avatar.png");
        userService.save(user);
        return "redirect:/personal";
    }


}
