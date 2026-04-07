package com.capstone.Jachwi_inServerSpring.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용 → 세션 사용 안 함
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로
                        .requestMatchers(
                                "/api/v1/users/join/**",
                                "/api/v1/users/login"
                        ).permitAll()
                        .requestMatchers("/api/v1/map/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/apt-trade/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/posts/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
