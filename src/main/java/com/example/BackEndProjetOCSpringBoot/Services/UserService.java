package com.example.BackEndProjetOCSpringBoot.Services;

import com.example.BackEndProjetOCSpringBoot.Models.User;
import com.example.BackEndProjetOCSpringBoot.Repositories.UserRepository;
import com.example.BackEndProjetOCSpringBoot.Interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User addUser(User user) {
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
