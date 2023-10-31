package org.example.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.dto.PlayerRequestDto;
import org.example.domain.dto.PlayerResponseDto;
import org.example.service.PlayerService;
import org.example.service.PlayerServiceImpl;

import java.io.IOException;
import java.util.Map;

/**
 * Сервлет для регистрации игрока
 */
@WebServlet("/registration")
public class PlayerRegistrationServlet extends HttpServlet {
    private PlayerService playerService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        playerService = PlayerServiceImpl.getInstance();
        objectMapper = new ObjectMapper();
    }

    /**
     * Метод для регистрации игрока
     *
     * @param req  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws IOException если произошла ошибка во время ответа
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            PlayerRequestDto newPlayer = objectMapper.readValue(req.getReader(), PlayerRequestDto.class);
            PlayerResponseDto playerResponseDto = playerService.registration(newPlayer);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(playerResponseDto));
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(Map.of("errorMessage", e.getMessage())));
        }
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
