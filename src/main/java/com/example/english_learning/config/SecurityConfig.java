package com.example.english_learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // ✔ Tắt CSRF khi dùng API
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth

                                // ✔ Những API không cần JWT
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/api/auth/**",   // Login, Register
                                        "/test"
                                ).permitAll()

                                // ✔ Các API có phân quyền
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/user/**").hasAnyRole("USER")

                                // 🔥 Khi DÙNG JWT → phải yêu cầu xác thực
                                // .anyRequest().authenticated()

                                // ❌ Khi KHÔNG dùng JWT → mở tất cả
                                .anyRequest().permitAll()
                )

                // ❌ Nếu KHÔNG dùng JWT thì tắt Basic Auth để tránh conflict
                .httpBasic(httpBasic -> httpBasic.disable());

        // 🔥 Khi DÙNG JWT → bật filter
//        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling(ex -> ex
//                        // Không có token → 401
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.setStatus(401);
//                            response.setContentType("application/json;charset=UTF-8");
//                            response.getWriter().write("{\"message\": \"Yêu cầu token để thực hiện dịch vụ\"}");
//                        })
//
//                        // Có token nhưng sai role → 403
//                        .accessDeniedHandler((request, response, accessDeniedException) -> {
//                            response.setStatus(403);
//                            response.setContentType("application/json;charset=UTF-8");
//                            response.getWriter().write("{\"message\": \"Bạn không có quyền để thực hiện dịch vụ này\"}");
//                        })
//                );


        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
