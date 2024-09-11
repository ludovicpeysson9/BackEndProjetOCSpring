package com.example.BackEndProjetOCSpringBoot.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.BackEndProjetOCSpringBoot.Interfaces.CloudinaryServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service to handle image uploads to Cloudinary.
 */
@Service
public class CloudinaryService implements CloudinaryServiceInterface {

    private final Cloudinary cloudinaryConfig;

    /**
     * Constructs a CloudinaryService with necessary configuration.
     * @param cloudinaryConfig Cloudinary configuration provided at application startup.
     */
                    
    public CloudinaryService(Cloudinary cloudinaryConfig) {
        this.cloudinaryConfig = cloudinaryConfig;
    }

    /**
     * Uploads an image to Cloudinary and returns the URL of the uploaded image.
     * @param file The image file to upload.
     * @return The URL of the uploaded image if successful, null otherwise.
     */
    @Override
    public String uploadImage(MultipartFile file) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinaryConfig.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
