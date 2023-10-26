package org.example.in.servlets.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.util.JwtUtil;

import java.io.IOException;

@WebFilter("/players/*")
public class JwtAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String token = httpRequest.getHeader("Authorization");

        if (token != null) {
            try {
                Jwts.parser()
                        .setSigningKey(JwtUtil.secret)
                        .parseClaimsJws(token)
                        .getBody();
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (ExpiredJwtException e) {
                // Токен истек
                ((HttpServletResponse) servletResponse)
                        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
            } catch (Exception e) {
                // Токен не валиден
                ((HttpServletResponse) servletResponse)
                        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            }
        } else {
            // Токен отсутствует или имеет неверный формат
            ((HttpServletResponse) servletResponse)
                    .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
        }
    }
}
