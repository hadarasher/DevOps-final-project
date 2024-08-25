package hit.final_project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder sensitiveDataEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/jobs/**").permitAll() // Allow public access to /api/jobs/*
                                .anyRequest().authenticated() // Other requests require authentication
                )
                .csrf(csrf -> csrf.disable()); // Disable CSRF for simplicity (only do this for non-production environments)

        return http.build();
    }
}
