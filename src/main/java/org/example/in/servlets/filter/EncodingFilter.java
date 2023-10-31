package org.example.in.servlets.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

/**
 * Сервлет фильтр для настройки кодировки и содержания при ответе.
 */
@WebFilter("/*")
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String encoding = "UTF-8";
        servletRequest.setCharacterEncoding(encoding);
        servletResponse.setCharacterEncoding(encoding);
        String contentType = "application/json";
        servletResponse.setContentType(contentType);
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
