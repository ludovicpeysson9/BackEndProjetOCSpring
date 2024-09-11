package com.example.BackEndProjetOCSpringBoot.Controllers;

import com.example.BackEndProjetOCSpringBoot.DTOs.UserDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Validated
@Tag(name = "User", description = "API for managing users")
public class UserController {

    private final UserServiceInterface userService;

    public UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a user by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable @Min(1) Integer id) {
        try {
            return findUserById(id);
        } catch (Exception e) {
            return handleInternalServerError(e);
        }
    }

    private ResponseEntity<Object> findUserById(Integer id) {
        UserDTO userDTO = userService.findById(id);
        return (userDTO != null)
                ? ResponseEntity.ok(userDTO)
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + id + " not found.");
    }

    private ResponseEntity<Object> handleInternalServerError(Exception e) {
        System.err.println("Internal server error: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the request.");
    }
}