package com.example.BackEndProjetOCSpringBoot.DTOs;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
