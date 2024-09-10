package com.example.BackEndProjetOCSpringBoot.DTOs;

import lombok.Data;

@Data
public class MessageRequestDTO {
    private Integer rental_id;
    private Integer user_id;
    private String message;
}
