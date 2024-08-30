package com.example.BackEndProjetOCSpringBoot.Services;

import com.example.BackEndProjetOCSpringBoot.Models.User;
import com.example.BackEndProjetOCSpringBoot.Repositories.UserRepository;
import com.example.BackEndProjetOCSpringBoot.Interfaces.CustomUserDetailsServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements CustomUserDetailsServiceInterface {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        System.out.println("User found: " + user.getEmail() + ", password: " + user.getPassword());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
