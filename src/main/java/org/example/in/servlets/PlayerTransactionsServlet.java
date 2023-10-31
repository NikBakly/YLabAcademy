package org.example.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.dto.PlayerResponseDto;
import org.example.domain.dto.TransactionRequestDto;
import org.example.exception.InvalidInputException;
import org.example.service.PlayerService;
import org.example.service.PlayerServiceImpl;
import org.example.util.JwtUtil;
import org.example.util.TransactionType;

import java.io.IOException;
import java.util.Map;

/**
 * Сервлет для обработки транзакционных действий игрока
 */
@WebServlet("/players/transaction")
public class PlayerTransactionsServlet extends HttpServlet {
    private PlayerService playerService;
    private ObjectMapper objectMapper;

    /**
     * Метод для создания транзакции типа дебит или кредит
     *
     * @param req  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws IOException если во время ответа произошла ошибка
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            TransactionRequestDto transactionRequestDto =
                    objectMapper.readValue(req.getReader(), TransactionRequestDto.class);
            checkTransactionDto(transactionRequestDto);

            String jwtToken = req.getHeader("Authorization");
            String loginPlayer = Jwts.parser()
                    .setSigningKey(JwtUtil.secret)
                    .parseClaimsJws(jwtToken)
                    .getBody().get("login", String.class);
            PlayerResponseDto playerResponseDto =
                    transactionRequestDto.type().equals(TransactionType.CREDIT) ?
                            playerService.creditForPlayer(loginPlayer, transactionRequestDto) :
                            playerService.debitForPlayer(loginPlayer, transactionRequestDto);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(playerResponseDto));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(Map.of("errorMessage", e.getMessage())));
        }
    }

    /**
     * Метод для валидации данных дто объекта
     *
     * @param transactionRequestDto дто объект транзакции
     * @throws InvalidInputException если объект не прошел проверку
     */
    private void checkTransactionDto(TransactionRequestDto transactionRequestDto) throws InvalidInputException {
        if (transactionRequestDto.id() == null ||
                transactionRequestDto.size() == null ||
                transactionRequestDto.type() == null) {
            throw new InvalidInputException("Один или несколько значений транзакций пустые.");
        }
        if (transactionRequestDto.size() < 0) {
            throw new InvalidInputException("Размер транзакции не должен быть нулем.");
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
