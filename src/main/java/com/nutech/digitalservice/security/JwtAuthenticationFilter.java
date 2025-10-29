package com.nutech.digitalservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutech.digitalservice.dto.WebResponse;
import com.nutech.digitalservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String requestPath = request.getRequestURI();

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Check if Authorization header is missing for protected endpoints
        if (authorizationHeader == null) {
            writeJwtErrorResponse(response);
            return;
        }

        if (authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                writeJwtErrorResponse(response);
                return;
            }
        } else {
            // Authorization header exists but doesn't start with "Bearer "
            writeJwtErrorResponse(response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    writeJwtErrorResponse(response);
                    return;
                }
            } catch (Exception e) {
                writeJwtErrorResponse(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeJwtErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        WebResponse<Object> errorResponse = WebResponse.builder()
                .status(108)
                .message("Token tidak tidak valid atau kadaluwarsa")
                .data(null)
                .build();

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    private boolean isPublicEndpoint(String requestPath) {
        return requestPath.startsWith("/api-docs") ||
               requestPath.startsWith("/v3/api-docs") ||
               requestPath.startsWith("/swagger-ui") ||
               requestPath.equals("/swagger-ui.html") ||
               requestPath.startsWith("/uploads") ||
               requestPath.startsWith("/h2-console") ||
               requestPath.startsWith("/api/v1/registration") ||
               requestPath.startsWith("/api/v1/login") ||
               requestPath.equals("/api/v1/banner");
    }
}