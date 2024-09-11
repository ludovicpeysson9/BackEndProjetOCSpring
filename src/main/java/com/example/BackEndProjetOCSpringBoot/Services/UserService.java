package com.example.BackEndProjetOCSpringBoot.Services;

import com.example.BackEndProjetOCSpringBoot.Models.User;
import com.example.BackEndProjetOCSpringBoot.Repositories.UserRepository;
import com.example.BackEndProjetOCSpringBoot.DTOs.UserDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.UserServiceInterface;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service providing user management operations.
 */
@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a UserService with necessary dependencies.
     * 
     * @param userRepository  The repository for user data access.
     * @param passwordEncoder The encoder for hashing passwords.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Converts a User entity to a UserDTO.
     * 
     * @param user The User entity to convert.
     * @return The UserDTO or null if user is null.
     */
    private UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .created_at(user.getCreated_at())
                .updated_at(user.getUpdated_at())
                .build();
    }

    /**
     * Saves a user in the database.
     * 
     * @param user The User to save.
     * @return The saved User with encrypted password.
     */
    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Finds a user by email.
     * 
     * @param email The email to search for.
     * @return The User, or throws if not found.
     */
    @Override
    public User findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.findByEmail(email);
    }

    /**
     * Checks if an email is already used by a user.
     * 
     * @param email The email to check.
     * @return true if the email is in use, false otherwise.
     */
    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.existsByEmail(email);
    }

    /**
     * Finds a user by their ID.
     * 
     * @param id The ID of the user.
     * @return The UserDTO or throws if not found.
     */
    @Override
    public UserDTO findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }
}
