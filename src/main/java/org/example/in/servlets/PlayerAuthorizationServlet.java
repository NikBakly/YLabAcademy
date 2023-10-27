package org.example.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.PlayerRequestDto;
import org.example.dto.PlayerResponseDto;
import org.example.service.PlayerService;
import org.example.service.PlayerServiceImpl;
import org.example.util.JwtUtil;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Сервлет для авторизации игрока
 */
@WebServlet("/authorization")
public class PlayerAuthorizationServlet extends HttpServlet {
    private PlayerService playerService;
    private ObjectMapper objectMapper;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            PlayerRequestDto playerRequestDto = objectMapper.readValue(req.getReader(), PlayerRequestDto.class);
            PlayerResponseDto playerResponseDto = playerService.authorization(playerRequestDto);
            String jwtToken = Jwts.builder()
                    .setSubject("authorization")
                    .claim("id", playerResponseDto.id())
                    .claim("login", playerResponseDto.login())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + JwtUtil.oneHourInMilliseconds))
                    .signWith(SignatureAlgorithm.HS256, JwtUtil.secret)
                    .compact();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(Map.of("token", jwtToken)));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(Map.of("errorMessage", e.getMessage())));
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        playerService = PlayerServiceImpl.getInstance();
        objectMapper = new ObjectMapper();
        super.init(config);
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
