package com.example.BackEndProjetOCSpringBoot.Config;

import com.example.BackEndProjetOCSpringBoot.Security.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    // Définition du bean PasswordEncoder qui sera injecté partout
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Utilisation de BCryptPasswordEncoder comme méthode d'encodage par défaut
        return new BCryptPasswordEncoder();
    }

    // Configuration du SecurityFilterChain pour gérer la sécurité des requêtes HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exceptionHandling -> 
                exceptionHandling.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(sessionManagement -> 
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()  
                // .requestMatchers("/api/auth/**").permitAll() // Permet l'accès sans authentification aux endpoints /api/auth/**
                // .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
            );

        // Ajout du filtre JWT avant le UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Construction de la chaîne de filtres de sécurité
    }

    // Fournit l'AuthenticationManager utilisé pour l'authentification
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
