package org.example.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.dto.AuditResponseDto;
import org.example.service.AuditService;
import org.example.service.AuditServiceImpl;
import org.example.util.JwtUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/players/audits")
public class PlayerAuditServlet extends HttpServlet {
    private AuditService auditService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        auditService = AuditServiceImpl.getInstance();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        super.init(config);
    }

    /**
     * Метод для получения аудитов игрока
     *
     * @param req  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws IOException если произошла ошибка во время ответа
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jwtToken = req.getHeader("Authorization");
        Long playerId = getPlayerIdByJwtToken(jwtToken);
        List<AuditResponseDto> auditsResponseDto =
                auditService.findAuditsByLoginPlayer(playerId);
        resp.getWriter().write(objectMapper.writeValueAsString(auditsResponseDto));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Метод для получения id игрока из jwt-токена
     *
     * @param jwtToken jwt-токен игрока при запросе
     * @return id игрока
     */
    private Long getPlayerIdByJwtToken(String jwtToken) {
        return Jwts.parser()
                .setSigningKey(JwtUtil.secret)
                .parseClaimsJws(jwtToken)
                .getBody().get("id", Long.class);
    }
}
