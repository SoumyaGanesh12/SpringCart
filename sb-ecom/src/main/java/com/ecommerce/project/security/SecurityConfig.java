package com.ecommerce.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;
		
	// BCrypt password encoder bean
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// Authentication Manager bean
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // Security filter chain
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			.csrf(csrf -> csrf.disable()) // Disable csrf fro REST API
			.authorizeHttpRequests(auth -> auth
					// Public endpoints (no authentication required)
					.requestMatchers("/api/auth/**").permitAll()
					.requestMatchers("/api/public/**").permitAll()
					.requestMatchers("/h2-console/**").permitAll()
					
					// Admin endpoints
					.requestMatchers("/api/admin/**").hasRole("ADMIN")
					
					// All other endpoints require authentication
					.anyRequest().authenticated()
			)
			// No sessions, need to use JWT
			.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
			// Add JWT filter
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		
		// For H2 console 
		http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
		
		return http.build();
	}
	
}
