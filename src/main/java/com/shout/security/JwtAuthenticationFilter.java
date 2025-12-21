package com.shout.security;

import com.shout.model.User;
import com.shout.repository.UserRepository;
import com.shout.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * JWT Authentication Filter
 * Validates JWT tokens on protected routes and extracts user from token
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(token);

                if (userId != null) {
                    Optional<User> userOptional = userRepository.findById(userId);

                    if (userOptional.isPresent()) {
                        User user = userOptional.get();

                        // Check if user is banned
                        if (user.getAccountBanned() != null && user.getAccountBanned()) {
                            log.warn("🚫 Banned user attempted access: {}", user.getUsername());
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\":\"Account banned\",\"code\":\"ACCOUNT_BANNED\"}");
                            return;
                        }

                        // Attach user to request
                        request.setAttribute("user", user);
                        request.setAttribute("userId", userId);
                        log.debug("✅ Authenticated user: {} (ID: {})", user.getUsername(), userId);
                    } else {
                        log.warn("⚠️ User not found for token: {}", userId);
                    }
                }
            }

        } catch (Exception e) {
            log.error("❌ JWT authentication error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        return path.startsWith("/api/auth/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/health") ||
               path.equals("/") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/");
    }
}
