package org.example.in.controller.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.in.filter.JwtAuthorizationFilter;
import org.example.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static org.mockito.Mockito.*;

public class JwtAuthorizationFilterTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthorizationFilter filter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthorizationFilter();
    }

    @Test
    @DisplayName("Валидация верного jwt-токена")
    public void testValidToken() throws Exception {
        String token = Jwts.builder()
                .setSubject("authorization")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtUtil.oneHourInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, JwtUtil.secret)
                .compact();

        when(request.getHeader("Authorization")).thenReturn(token);
        when(request.getServletPath()).thenReturn("players/transactions");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    @DisplayName("Валидация устаревшего jwt-токена")
    public void testExpiredToken() throws Exception {
        String token = Jwts.builder()
                .setSubject("authorization")
                .setIssuedAt(new Date(System.currentTimeMillis() - JwtUtil.oneHourInMilliseconds))
                .setExpiration(new Date())
                .signWith(SignatureAlgorithm.HS256, JwtUtil.secret)
                .compact();

        when(request.getHeader("Authorization")).thenReturn(token);
        when(request.getServletPath()).thenReturn("players/transactions");

        filter.doFilter(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Валидация неверного jwt-токена")
    public void testInvalidToken() throws Exception {
        String token = "invalid_token";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(request.getServletPath()).thenReturn("players/transactions");

        filter.doFilter(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Валидация пустого jwt-токена")
    public void testMissingToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getServletPath()).thenReturn("players/transactions");

        filter.doFilter(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
        verify(filterChain, never()).doFilter(request, response);
    }
}