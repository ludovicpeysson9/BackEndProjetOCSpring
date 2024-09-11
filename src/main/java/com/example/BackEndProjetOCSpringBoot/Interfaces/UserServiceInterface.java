package com.example.BackEndProjetOCSpringBoot.Interfaces;

import com.example.BackEndProjetOCSpringBoot.DTOs.UserDTO;
import com.example.BackEndProjetOCSpringBoot.Models.User;

public interface UserServiceInterface{
    User saveUser(User user);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    UserDTO findById(Integer id);
}
