package com.example.english_learning.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of(
                "cloud_name", "dvkaneet8",
                "api_key", "349725877586597",
                "api_secret", "kyR-VTTaV8aliJU-yOBWHD19qeM"
        ));
    }
}
