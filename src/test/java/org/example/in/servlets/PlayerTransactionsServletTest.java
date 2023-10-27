package org.example.in.servlets;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.PlayerResponseDto;
import org.example.dto.TransactionRequestDto;
import org.example.service.PlayerService;
import org.example.util.JwtUtil;
import org.example.util.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Mockito.*;

class PlayerTransactionsServletTest {
    @Mock
    private PlayerService playerService;

    private PlayerTransactionsServlet servlet;
    private String loginPlayer;

    String jwtToken;

    @BeforeEach
    void setUp() throws ServletException {
        MockitoAnnotations.openMocks(this);
        servlet = new PlayerTransactionsServlet();
        servlet.init(mock(ServletConfig.class));
        servlet.setPlayerService(playerService);
        Long playerId = 1L;
        loginPlayer = "tester";

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
    @DisplayName("Успешное создания транзакции типа кредит")
    public void creditTransaction() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        TransactionRequestDto transactionRequestDto = new TransactionRequestDto(1L, TransactionType.CREDIT, 1200.0);
        PlayerResponseDto playerResponseDto = new PlayerResponseDto(1L, "login", BigDecimal.valueOf(0.0));

        when(request.getHeader("Authorization")).thenReturn(jwtToken);
        when(playerService.creditForPlayer(loginPlayer, transactionRequestDto)).thenReturn(playerResponseDto);

        String jsonRequest = "{\"id\":\"1\", \"type\":\"CREDIT\", \"size\":\"1200\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doPost(request, response);

        verify(request).getReader();
        verify(playerService).creditForPlayer(loginPlayer, transactionRequestDto);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).getWriter();
    }

    @Test
    @DisplayName("Успешное создания транзакции типа дебит")
    public void debitTransaction() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        TransactionRequestDto transactionRequestDto = new TransactionRequestDto(1L, TransactionType.DEBIT, 1200.0);
        PlayerResponseDto playerResponseDto = new PlayerResponseDto(1L, "login", BigDecimal.valueOf(0.0));

        when(playerService.debitForPlayer(loginPlayer, transactionRequestDto)).thenReturn(playerResponseDto);
        when(request.getHeader("Authorization")).thenReturn(jwtToken);

        String jsonRequest = "{\"id\":\"1\", \"type\":\"DEBIT\", \"size\":\"1200\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doPost(request, response);

        verify(request).getReader();
        verify(playerService).debitForPlayer(loginPlayer, transactionRequestDto);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).getWriter();
    }

    @Test
    @DisplayName("Не успешное создания транзакции типа дебит при size = null")
    public void debitTransactionWhenSizeIsNull() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        TransactionRequestDto transactionRequestDto = new TransactionRequestDto(1L, TransactionType.DEBIT, 1200.0);
        PlayerResponseDto playerResponseDto = new PlayerResponseDto(1L, "login", BigDecimal.valueOf(0.0));

        when(playerService.debitForPlayer(loginPlayer, transactionRequestDto)).thenReturn(playerResponseDto);
        when(request.getHeader("Authorization")).thenReturn(jwtToken);

        String jsonRequest = "{\"id\":\"1\", \"type\":\"DEBIT\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doPost(request, response);

        verify(request).getReader();
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).getWriter();
    }

    @Test
    @DisplayName("Не успешное создания транзакции типа дебит при size < 0")
    public void debitTransactionWhenSizeLessThanZero() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        TransactionRequestDto transactionRequestDto = new TransactionRequestDto(1L, TransactionType.DEBIT, 1200.0);
        PlayerResponseDto playerResponseDto = new PlayerResponseDto(1L, "login", BigDecimal.valueOf(0.0));

        when(playerService.debitForPlayer(loginPlayer, transactionRequestDto)).thenReturn(playerResponseDto);
        when(request.getHeader("Authorization")).thenReturn(jwtToken);

        String jsonRequest = "{\"id\":\"1\", \"type\":\"DEBIT\", \"size\":\"-10\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doPost(request, response);

        verify(request).getReader();
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).getWriter();
    }

}