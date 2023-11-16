package com.helendong.quiz.quizapp.service;

import com.helendong.quiz.quizapp.model.User;
import com.helendong.quiz.quizapp.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register (User newUser) {
        try {
            validateUser(newUser);
            User registeredUser = saveUser(newUser);
            return registeredUser;
        } catch (ValidationException ve) {
            throw new ValidationException("User registration validation failed: " + ve.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("User registration failed: " + e.getMessage());
        }

    }

    private void validateUser(User newUser) {
        String username = newUser.getUsername();
        if (username == null || username.isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }

        String password = newUser.getPassword();
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }

        String email = newUser.getEmail();
        if (email == null || email.isEmpty() || !email.matches(".+@.+\\..+")) {
            throw new ValidationException("Invalid email address");
        }
    }


    @Transactional
    private User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception ex) {
            throw new RuntimeException("User registration failed: " + ex.getMessage());
        }
    }




}