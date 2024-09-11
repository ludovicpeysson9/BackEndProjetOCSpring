package com.example.BackEndProjetOCSpringBoot.DTOs;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private Timestamp created_at;
    private Timestamp updated_at;
}
