package com.example.BackEndProjetOCSpringBoot.Interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryServiceInterface {
    String uploadImage(MultipartFile file);
}
