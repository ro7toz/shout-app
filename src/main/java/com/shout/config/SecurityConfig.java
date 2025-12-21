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
            // CSRF Configuration - Disable for API endpoints
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/api/**",              // All REST API endpoints
                    "/oauth2/**",           // OAuth callbacks
                    "/auth/callback/**",    // Auth callbacks
                    "/actuator/**"          // Actuator endpoints
                )
            )
            
            // CORS Configuration
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
                    "/sitemap.xml"
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
                
                // Health check endpoints
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/health/**"
                ).permitAll()
                
                // Public API endpoints
                .requestMatchers(
                    "/api/auth/**",
                    "/api/public/**"
                ).permitAll()
                
                // Protected endpoints - require authentication
                .anyRequest().authenticated()
            )
            
            // Session Management - Stateless for APIs
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // OAuth2 Login Configuration
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
            )
            
            // Logout Configuration
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            
            // Security Headers
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' cdn.tailwindcss.com unpkg.com connect.facebook.net; " +
                    "style-src 'self' 'unsafe-inline' cdn.tailwindcss.com fonts.googleapis.com; " +
                    "font-src 'self' fonts.gstatic.com; " +
                    "img-src 'self' data: https: *.fbcdn.net *.cdninstagram.com; " +
                    "connect-src 'self' https://graph.facebook.com https://graph.instagram.com"
                ))
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss.disable()) // Modern browsers use CSP
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed origins - Update for production
        List<String> allowedOrigins = Arrays.asList(
            "http://localhost:8080",
            "http://localhost:3000",
            "http://localhost:5173",
            "http://127.0.0.1:8080",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173",
            "https://shoutx.co.in",
            "https://www.shoutx.co.in",
            "https://shoutx-prod.elasticbeanstalk.com"
        );
        
        configuration.setAllowedOrigins(allowedOrigins);
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
