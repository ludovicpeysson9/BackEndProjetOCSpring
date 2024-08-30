package com.example.BackEndProjetOCSpringBoot.Controllers;

import com.example.BackEndProjetOCSpringBoot.DTOs.JwtResponseDTO;
import com.example.BackEndProjetOCSpringBoot.DTOs.LoginRequestDTO;
import com.example.BackEndProjetOCSpringBoot.DTOs.SignupRequestDTO;
import com.example.BackEndProjetOCSpringBoot.Models.User;
import com.example.BackEndProjetOCSpringBoot.Services.UserService;
import com.example.BackEndProjetOCSpringBoot.Config.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDTO signUpRequest) {
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already taken!");
        }

        // Create new user's account 
        User user = new User(
                null, // ID will be generated automatically
                signUpRequest.getEmail(),
                signUpRequest.getName(),
                signUpRequest.getPassword(), // use the already encoded password
                null, // created_at
                null // updated_at
        );

        userService.saveUser(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        System.out.println("Attempting login with email: " + loginRequest.getEmail() + " and raw password: "
                + loginRequest.getPassword());

        // Récupération de l'utilisateur depuis la base de données
        User user = userService.findByEmail(loginRequest.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: User not found");
        }

        System.out.println("Encoded password in DB: " + user.getPassword());

        // Comparaison du mot de passe fourni avec celui en base
        boolean matches = encoder.matches(loginRequest.getPassword(), user.getPassword());
        System.out.println("Password matches: " + matches);

        if (!matches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: Incorrect password");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(loginRequest.getEmail());
            return ResponseEntity.ok(new JwtResponseDTO(jwt));
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(user);
    }
}
