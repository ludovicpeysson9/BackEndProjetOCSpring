package com.example.BackEndProjetOCSpringBoot.DTOs;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String name;
    private String email;
    private String password;
}
