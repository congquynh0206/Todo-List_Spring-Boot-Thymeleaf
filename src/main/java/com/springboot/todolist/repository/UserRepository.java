package com.springboot.todolist.repository;

import com.springboot.todolist.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(String role);
    Page<User> findByRole (String role, Pageable pageable);
}

