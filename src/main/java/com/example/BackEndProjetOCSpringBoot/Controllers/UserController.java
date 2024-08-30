// package com.example.BackEndProjetOCSpringBoot.Controllers;

// import com.example.BackEndProjetOCSpringBoot.Interfaces.UserServiceInterface;
// import com.example.BackEndProjetOCSpringBoot.Models.User;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.List;

// @RestController
// @RequestMapping("/users")
// public class UserController {

//     @Autowired
//     private UserServiceInterface userService;

//     @PostMapping("/add")
//     public User addUser(@RequestBody User user) {
//         return userService.addUser(user);
//     }

//     @GetMapping("/all")
//     public List<User> getAllUsers() {
//         return userService.getAllUsers();
//     }
// }