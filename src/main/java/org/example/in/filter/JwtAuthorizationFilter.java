package org.example.in.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.example.util.JwtUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет фильтр для фильтрации приходящих jwt-токенов.
 */
@WebFilter("/players/*")
public class JwtAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        System.out.println(httpRequest.getServletPath());
        System.out.println("empty context path ((");
        if (httpRequest.getServletPath().equals("/players/registration") ||
                httpRequest.getServletPath().equals("/players/authorization")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
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
