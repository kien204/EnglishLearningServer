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
        Server ngrokServer = new Server()
                .url("https://nonvoluntary-dianoetically-marilynn.ngrok-free.dev")
                .description("Ngrok Tunnel");

        Server localServer = new Server()
                .url("http://localhost:8082")
                .description("Local Server");

        return new OpenAPI()

                // 🔥 Bật JWT để có nút Authorize trên Swagger
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                        // cmt nếu k cần jwt
//                                .scheme("bearer")
//                                .bearerFormat("JWT")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

//                .servers(List.of(
//                        new Server().url("http://localhost:8082").description("Local server")
//                ))
                .servers(List.of(localServer, ngrokServer))
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
