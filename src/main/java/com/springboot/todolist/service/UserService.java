package com.springboot.todolist.service;

import com.springboot.todolist.repository.UserRepository;
import com.springboot.todolist.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // Service register
    public User save(User user){
        return userRepository.save(user);
    }

    // Find email to log in
    public Optional<User> findByEmail (String email){
        return userRepository.findByEmail(email);
    }

    // Cập nhật display name
    public void updateDisplayName(String email, String newDisplayName) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setDisplayName(newDisplayName);
            userRepository.save(user);
        }
    }

    // Xóa bằng ID
    public void deleteById (long id){
        userRepository.deleteById(id);
    }

    // Cập nhật email
    public void updateEmail(String oldEmail, String newEmail) {
        Optional<User> userOpt = userRepository.findByEmail(oldEmail);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmail(newEmail);
            userRepository.save(user);
        }
    }

    // Cập nhật password
    public void updatePassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            userRepository.save(user);
        }
    }
    // Reset password
    public void resetPassword(long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword("123456");
            userRepository.save(user);
        }
    }

    // Set Role
    public void setRole(long id, String newRole) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            userRepository.save(user);
        }
    }

    // Get All User by role
    public List<User> getUserByRole (String role){
        return userRepository.findAllByRole(role);
    }


    public Optional<User> findById (long id){
        return userRepository.findById(id);
    }
}
