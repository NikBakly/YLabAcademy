package org.example.in.servlets;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.AuditService;
import org.example.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

class PlayerAuditServletTest {
    @Mock
    AuditService auditService;

    private PlayerAuditServlet servlet;
    private Long playerId;
    private String jwtToken;


    @BeforeEach
    void setUp() throws ServletException {
        MockitoAnnotations.openMocks(this);
        servlet = new PlayerAuditServlet();
        servlet.init(mock(ServletConfig.class));
        servlet.setAuditService(auditService);

        playerId = 1L;
        String loginPlayer = "tester";

        jwtToken = Jwts.builder()
                .setSubject("authorization")
                .claim("id", playerId)
                .claim("login", loginPlayer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtUtil.oneHourInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, JwtUtil.secret)
                .compact();
    }

    @Test
    @DisplayName("Успешно нахождения всех историй типа дебит")
    void creditHistoryTransactions() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader("Authorization")).thenReturn(jwtToken);
        when(auditService.findAuditsByLoginPlayer(playerId)).thenReturn(List.of());

        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doGet(request, response);

        verify(auditService).findAuditsByLoginPlayer(playerId);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).getWriter();
    }

}