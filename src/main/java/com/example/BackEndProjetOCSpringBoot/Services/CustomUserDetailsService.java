package com.example.BackEndProjetOCSpringBoot.Services;

import com.example.BackEndProjetOCSpringBoot.Models.User;
import com.example.BackEndProjetOCSpringBoot.Repositories.UserRepository;
import com.example.BackEndProjetOCSpringBoot.Interfaces.CustomUserDetailsServiceInterface;

import org.springframework.util.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

/**
 * Service for loading user-specific data that handles the core user
 * authentication logic.
 */
@Service
public class CustomUserDetailsService implements CustomUserDetailsServiceInterface {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user details required by the authentication manager to perform
     * authentication.
     * 
     * @param email The email of the user attempting to log in.
     * @return UserDetails required for authentication.
     * @throws UsernameNotFoundException if the email is not valid or the user is
     *                                   not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (!isValidEmail(email)) {
            throw new UsernameNotFoundException("Invalid email provided");
        }

        User user = findUserByEmail(email);
        return createSpringUser(user);
    }

    /**
     * Validates the email format.
     * 
     * @param email The email to validate.
     * @return true if the email is non-null and has text; false otherwise.
     */
    private boolean isValidEmail(String email) {
        return StringUtils.hasText(email);
    }

    /**
     * Retrieves a user by email.
     * 
     * @param email The email to search for.
     * @return The User object corresponding to the email.
     * @throws UsernameNotFoundException if no user is associated with the email
     *                                   provided.
     */
    private User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        System.out.println("User found: " + user.getEmail() + ", password: " + user.getPassword());
        return user;
    }

    /**
     * Converts a User model to a Spring Security UserDetails object.
     * 
     * @param user The User entity to convert.
     * @return UserDetails object for authentication purposes.
     */
    private UserDetails createSpringUser(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // No authorities are assigned to the user.
        );
    }
}
