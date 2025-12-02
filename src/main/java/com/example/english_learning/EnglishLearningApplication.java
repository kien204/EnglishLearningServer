package com.example.english_learning;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnglishLearningApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EnglishLearningApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Lấy port, mặc định 8082


        String port = System.getProperty("server.port", "8082");

        // URL Swagger hoặc frontend
        String swaggerUrl = "http://localhost:" + port + "/swagger-ui/index.html";

        // In ra consoleq
        System.out.println("🔗 Swagger UI: " + swaggerUrl);

    }
}
