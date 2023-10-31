package org.example.in.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.dto.PlayerRequestDto;
import org.example.domain.dto.PlayerResponseDto;
import org.example.service.PlayerService;
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

import static org.mockito.Mockito.*;

class PlayerRegistrationServletTest {
    @Mock
    private PlayerService playerService;

    private PlayerRegistrationServlet servlet;

    @BeforeEach
    public void setUp() throws ServletException {
        MockitoAnnotations.openMocks(this);
        servlet = new PlayerRegistrationServlet();
        servlet.init(mock(ServletConfig.class));
        servlet.setPlayerService(playerService);
    }

    @Test
    @DisplayName("Успешная регистрация игрока")
    public void registrationPlayer() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        PlayerRequestDto playerRequestDto = new PlayerRequestDto("login", "password");
        PlayerResponseDto playerResponseDto = new PlayerResponseDto(1L, "login", BigDecimal.valueOf(0.0));

        when(playerService.registration(any(PlayerRequestDto.class))).thenReturn(playerResponseDto);

        String jsonRequest = "{\"login\":\"login\", \"password\":\"password\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doPost(request, response);

        verify(request).getReader();
        verify(playerService).registration(playerRequestDto);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).getWriter();
    }

    @Test
    @DisplayName("Не успешная регистрация игрока")
    public void registrationPlayerWithError() throws Exception {
        // Создайте фейковый запрос и ответ
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Создайте объекты для сериализации и десериализации JSON
        PlayerRequestDto playerRequestDto = new PlayerRequestDto("login", "password");

        // Задайте ожидаемое поведение для playerService
        when(playerService.registration(any(PlayerRequestDto.class)))
                .thenThrow(new RuntimeException("Ошибка при регистрации"));

        // Задайте JSON-строку для запроса (предполагается, что вы уже знаете структуру JSON)
        String jsonRequest = "{\"login\":\"login\", \"password\":\"password\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        // Вызовите метод сервлета
        servlet.doPost(request, response);

        // Проверьте, что методы response были вызваны с правильными параметрами
        verify(request).getReader();
        verify(playerService).registration(playerRequestDto);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).getWriter();
    }
}