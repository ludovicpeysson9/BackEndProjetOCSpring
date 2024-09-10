package com.example.BackEndProjetOCSpringBoot.DTOs;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class RentalResponseDTO {
    private Integer id;
    private String name;
    private Double surface;
    private Double price;
    private String picture;
    private String description;
    private Integer owner_id; 
    private Timestamp created_at;
    private Timestamp updated_at;
}


