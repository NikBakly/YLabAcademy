package org.example.in.servlets;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import org.example.util.DatabaseConnector;
import org.example.util.LiquibaseManager;

import java.io.IOException;

@WebFilter("/*")
public class ServletFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String encoding = "UTF-8";
        servletRequest.setCharacterEncoding(encoding);
        servletResponse.setCharacterEncoding(encoding);
        String contentType = "application/json";
        servletResponse.setContentType(contentType);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LiquibaseManager
                .runDatabaseMigrations(
                        DatabaseConnector.URL,
                        DatabaseConnector.USERNAME,
                        DatabaseConnector.PASSWORD);
        Filter.super.init(filterConfig);
    }
}
