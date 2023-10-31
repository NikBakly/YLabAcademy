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
import org.example.domain.dto.TransactionResponseDto;
import org.example.service.TransactionService;
import org.example.service.TransactionServiceImpl;
import org.example.util.JwtUtil;
import org.example.util.TransactionType;

import java.io.IOException;
import java.util.List;

/**
 * Сервлет для нахождения истории кредитов
 */
@WebServlet("/players/creditHistory")
public class PlayerCreditHistoryServlet extends HttpServlet {
    private TransactionService transactionService;
    private ObjectMapper objectMapper;

    /**
     * Метод для получения истории транзакций типа кредит
     *
     * @param req  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws IOException если произошла ошибка во время ответа
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jwtToken = req.getHeader("Authorization");
        Long playerId = getPlayerIdByJwtToken(jwtToken);
        List<TransactionResponseDto> transactionsResponseDto =
                transactionService.findHistoryTransactions(playerId, TransactionType.CREDIT);
        resp.getWriter().write(objectMapper.writeValueAsString(transactionsResponseDto));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        transactionService = TransactionServiceImpl.getInstance();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        super.init(config);
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
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
