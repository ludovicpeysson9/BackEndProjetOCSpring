package com.example.BackEndProjetOCSpringBoot.Controllers;

import com.example.BackEndProjetOCSpringBoot.DTOs.JwtResponseDTO;
import com.example.BackEndProjetOCSpringBoot.DTOs.LoginRequestDTO;
import com.example.BackEndProjetOCSpringBoot.DTOs.SignupRequestDTO;
import com.example.BackEndProjetOCSpringBoot.DTOs.UserDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.UserServiceInterface;
import com.example.BackEndProjetOCSpringBoot.Models.User;
import com.example.BackEndProjetOCSpringBoot.Config.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for user authentication and registration")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserServiceInterface userService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
            UserServiceInterface userService,
            PasswordEncoder encoder,
            JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @Operation(summary = "Register new user", description = "Registers a new user and returns a JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = JwtResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDTO signUpRequest) {
        try {
            ResponseEntity<?> validationResponse = validateSignupRequest(signUpRequest);
            if (validationResponse != null) {
                return validationResponse;
            }

            User user = createUser(signUpRequest);

            String jwt = jwtUtils.generateJwtToken(user.getEmail(), user.getId());

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            response.put("token", jwt);

            return buildSuccessResponse(jwt);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering the user.");
        }
    }

    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully", content = @Content(schema = @Schema(implementation = JwtResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            ResponseEntity<?> validationResponse = validateLoginRequest(loginRequest);
            if (validationResponse != null) {
                return validationResponse;
            }

            User user = verifyUserCredentials(loginRequest);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication failed: Invalid credentials");
            }

            performAuthentication(loginRequest);

            String jwt = jwtUtils.generateJwtToken(user.getEmail(), user.getId());

            return ResponseEntity.ok(new JwtResponseDTO(jwt));

        } catch (Exception e) {
            return buildErrorResponse("An error occurred during authentication.", e);
        }
    }

    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user based on the provided JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or missing token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            ResponseEntity<?> validationResponse = validateToken(token);
            if (validationResponse != null) {
                return validationResponse;
            }

            Integer userId = jwtUtils.getUserIdFromJwtToken(extractJwtFromToken(token));
            UserDTO user = userService.findById(userId);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found");
            }

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return buildErrorResponse("An error occurred while retrieving user info", e);
        }
    }

    private ResponseEntity<?> validateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Token is missing or malformed");
        }
        return null;
    }

    private String extractJwtFromToken(String token) {
        return token.substring(7);
    }

    private ResponseEntity<?> validateSignupRequest(SignupRequestDTO signUpRequest) {
        if (signUpRequest.getEmail() == null || signUpRequest.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Email is required!");
        }
        if (signUpRequest.getPassword() == null || signUpRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Password is required!");
        }
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already taken!");
        }
        return null;
    }

    private User createUser(SignupRequestDTO signUpRequest) {
        User user = new User(
                null,
                signUpRequest.getEmail(),
                signUpRequest.getName(),
                signUpRequest.getPassword(),
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now()));
        return userService.saveUser(user);
    }

    private ResponseEntity<Map<String, String>> buildSuccessResponse(String jwt) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> validateLoginRequest(LoginRequestDTO loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Email is required!");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Password is required!");
        }
        return null;
    }

    private User verifyUserCredentials(LoginRequestDTO loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());
        if (user == null || !encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return null;
        }
        return user;
    }

    private void performAuthentication(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private ResponseEntity<?> buildErrorResponse(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }

}
