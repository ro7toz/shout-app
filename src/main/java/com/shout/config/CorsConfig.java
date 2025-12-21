package com.shout.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;

/**
 * CORS Configuration for ShoutX API
 *
 * This configuration is REQUIRED for the React frontend to communicate with the backend.
 * Without this, you'll get CORS errors in the browser console.
 *
 * Location: src/main/java/com/shout/config/CorsConfig.java
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Allow these origins (adjust for your environment)
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",      // React dev server (Vite custom port)
                "http://localhost:5173",      // React dev server (Vite default port)
                "http://127.0.0.1:3000",      // Alternative localhost
                "http://127.0.0.1:5173",      // Alternative localhost
                "https://shoutx.co.in",       // Production frontend
                "https://www.shoutx.co.in"    // Production with www
        ));

        // Allow all headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Expose these headers to the frontend
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        // Cache preflight requests for 1 hour
        config.setMaxAge(3600L);

        // Apply this configuration to all endpoints
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}