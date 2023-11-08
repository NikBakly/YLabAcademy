package org.example.in.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.util.JwtUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Сервлет фильтр для фильтрации приходящих jwt-токенов.
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/players/registration") ||
                request.getServletPath().equals("/players/authorization")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization");

        if (token != null) {
            try {
                Jwts.parser()
                        .setSigningKey(JwtUtil.secret)
                        .parseClaimsJws(token)
                        .getBody();
                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                // Токен истек
                response
                        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
            } catch (Exception e) {
                // Токен не валиден
                response
                        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            }
        } else {
            // Токен отсутствует или имеет неверный формат
            response
                    .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
        }
    }

}
