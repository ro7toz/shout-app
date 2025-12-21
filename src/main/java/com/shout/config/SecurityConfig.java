package com.shout.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF - Disabled for REST API
            .csrf(csrf -> csrf.disable())
            
            // CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Authorization Rules
            .authorizeHttpRequests(authz -> authz
                // Public static resources
                .requestMatchers(
                    "/", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**",
                    "/favicon.ico",
                    "/robots.txt",
                    "/sitemap.xml",
                    "/static/**",
                    "/index.html"
                ).permitAll()
                
                // Public pages
                .requestMatchers(
                    "/login",
                    "/privacy",
                    "/terms",
                    "/data-deletion",
                    "/about",
                    "/contact",
                    "/faq"
                ).permitAll()
                
                // Health check
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/health/**"
                ).permitAll()
                
                // Public API endpoints
                .requestMatchers(
                    "/api/auth/**",
                    "/api/public/**",
                    "/oauth2/**",
                    "/auth/callback/**"
                ).permitAll()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Session Management - Stateless for API
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // OAuth2 Login - Instagram Only
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
            )
            
            // Logout
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed origins - Add your frontend URLs
        List<String> allowedOrigins = Arrays.asList(
            "http://localhost:3000",      // React dev server (Vite custom)
            "http://localhost:5173",      // React dev server (Vite default)
            "http://localhost:8080",      // Backend
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173",
            "http://127.0.0.1:8080",
            "https://shoutx.co.in",       // Production frontend
            "https://www.shoutx.co.in",
            "https://*.elasticbeanstalk.com"  // AWS Elastic Beanstalk
        );
        
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
