package com.example.BackEndProjetOCSpringBoot.DTOs;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String name;
    private String email;

    public UserDTO(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

}
