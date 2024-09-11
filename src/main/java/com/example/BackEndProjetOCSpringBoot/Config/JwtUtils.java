package com.example.BackEndProjetOCSpringBoot.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for managing JSON Web Tokens (JWTs).
 * Provides methods to generate, parse, and validate JWTs based on the application's security requirements.
 */
@Component
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Generates a signing key using the specified secret from application properties.
     * 
     * @return A cryptographic signing key for JWT processing.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token with specified claims based on username and user ID.
     * 
     * @param username The username for which the token is generated.
     * @param userId The user ID that will be included in the token's claims.
     * @return A JWT string that represents the claims passed.
     */
    public String generateJwtToken(String username, Integer userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extracts the username from the JWT token.
     * 
     * @param token The JWT token from which the username is to be extracted.
     * @return The username from the JWT token.
     */
    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Extracts the user ID from the JWT token.
     * 
     * @param token The JWT token from which the user ID is to be extracted.
     * @return The user ID if valid, null if the token is invalid or expired.
     */
    public Integer getUserIdFromJwtToken(String token) {
        try {
            if (!validateJwtToken(token)) {
                throw new IllegalArgumentException("Invalid or expired token");
            }
    
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.get("userId", Integer.class);
        } catch (Exception e) {
            System.err.println("Error extracting user ID from token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates the JWT token's integrity and expiration.
     * 
     * @param authToken The JWT token to be validated.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            // Log the exception and handle it
        }
        return false;
    }
}
