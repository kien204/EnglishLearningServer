package com.example.english_learning.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {

        Server autoServer = new Server()
                .url("/")
                .description("Auto detect environment (Local / LAN / Ngrok / Production)");

        return new OpenAPI()
                .servers(List.of(autoServer))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info()
                        .title("English Learning API")
                        .description("API documentation for the English Learning application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pham Trung Kien")
                                .email("phamkien20122004@gmail.com")
                        )
                );
    }
}
