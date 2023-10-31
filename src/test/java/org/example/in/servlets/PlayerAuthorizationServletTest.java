package org.example.in.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.dto.PlayerRequestDto;
import org.example.domain.dto.PlayerResponseDto;
import org.example.exception.InvalidInputException;
import org.example.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlayerAuthorizationServletTest {
    @Mock
    private PlayerService playerService;

    private PlayerAuthorizationServlet servlet;

    @BeforeEach
    public void setUp() throws ServletException {
        MockitoAnnotations.openMocks(this);
        servlet = new PlayerAuthorizationServlet();
        servlet.init(mock(ServletConfig.class));
        servlet.setPlayerService(playerService);
    }

    @Test
    @DisplayName("Успешная авторизация игрока")
    void authorizationPlayer() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        PlayerRequestDto playerRequestDto = new PlayerRequestDto("login", "password");
        PlayerResponseDto playerResponseDto = new PlayerResponseDto(1L, "login", BigDecimal.valueOf(0.0));

        when(playerService.authorization(any(PlayerRequestDto.class))).thenReturn(playerResponseDto);

        String jsonRequest = "{\"login\":\"login\", \"password\":\"password\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doPost(request, response);

        verify(request).getReader();
        verify(playerService).authorization(playerRequestDto);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).getWriter();
    }

    @Test
    @DisplayName("Не успешная авторизация игрока")
    void authorizationPlayerWithError() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        PlayerRequestDto playerRequestDto = new PlayerRequestDto("login", "password");

        when(playerService.authorization(any(PlayerRequestDto.class)))
                .thenThrow(new InvalidInputException("ошибка при авторизации"));

        String jsonRequest = "{\"login\":\"login\", \"password\":\"password\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doPost(request, response);

        verify(request).getReader();
        verify(playerService).authorization(playerRequestDto);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).getWriter();
    }


}