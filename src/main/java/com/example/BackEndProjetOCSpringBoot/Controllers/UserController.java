package com.example.BackEndProjetOCSpringBoot.Controllers;

import com.example.BackEndProjetOCSpringBoot.DTOs.UserDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.UserServiceInterface;
import com.example.BackEndProjetOCSpringBoot.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserServiceInterface userService; 

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        User user = userService.findById(id);  
        if (user != null) {
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail());
            return ResponseEntity.ok(userDTO);  // 
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  
        }
    }
}