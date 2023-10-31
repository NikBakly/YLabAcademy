package org.example.in.servlets.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    @DisplayName("Успешное прохождение")
    public void testValidToken() throws Exception {
        String token = Jwts.builder()
                .setSubject("authorization")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtUtil.oneHourInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, JwtUtil.secret)
                .compact();

        when(request.getHeader("Authorization")).thenReturn(token);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testExpiredToken() throws Exception {
        String token = Jwts.builder()
                .setSubject("authorization")
                .setIssuedAt(new Date(System.currentTimeMillis() - JwtUtil.oneHourInMilliseconds))
                .setExpiration(new Date())
                .signWith(SignatureAlgorithm.HS256, JwtUtil.secret)
                .compact();

        when(request.getHeader("Authorization")).thenReturn(token);

        filter.doFilter(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    public void testInvalidToken() throws Exception {
        String token = "invalid_token";

        when(request.getHeader("Authorization")).thenReturn(token);

        filter.doFilter(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Отсутствие токена")
    public void testMissingToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
        verify(filterChain, never()).doFilter(request, response);
    }
}