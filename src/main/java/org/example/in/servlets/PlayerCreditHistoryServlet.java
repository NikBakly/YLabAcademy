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
import org.example.dto.TransactionResponseDto;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jwtToken = req.getHeader("Authorization");
        Long playerId = Jwts.parser()
                .setSigningKey(JwtUtil.secret)
                .parseClaimsJws(jwtToken)
                .getBody().get("id", Long.class);
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
}
