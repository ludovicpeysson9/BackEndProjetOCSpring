package com.example.BackEndProjetOCSpringBoot.Interfaces;

import com.example.BackEndProjetOCSpringBoot.Models.User;
import java.util.List;

public interface UserServiceInterface {
    User addUser(User user);
    List<User> getAllUsers();
}
