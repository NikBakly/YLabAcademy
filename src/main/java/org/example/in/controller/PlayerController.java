package org.example.in.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.domain.dto.*;
import org.example.exception.InvalidInputException;
import org.example.service.AuditService;
import org.example.service.PlayerService;
import org.example.service.TransactionService;
import org.example.util.JwtUtil;
import org.example.util.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api(tags = "PlayerController")
@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;
    private final TransactionService transactionService;
    private final AuditService auditService;

    @Autowired
    public PlayerController(PlayerService playerService, TransactionService transactionService, AuditService auditService) {
        this.playerService = playerService;
        this.transactionService = transactionService;
        this.auditService = auditService;
    }

    /**
     * Метод для регистрации игрока
     *
     * @param newPlayer информация об игроке
     * @return ответ на HTTP-запрос
     */
    @ApiOperation("Метод для регистрации игрока")
    @PostMapping(value = "/registration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlayerResponseDto> registration(@RequestBody PlayerRequestDto newPlayer) {
        PlayerResponseDto playerResponseDto = playerService.registration(newPlayer);
        return ResponseEntity.status(HttpStatus.CREATED).body(playerResponseDto);
    }

    /**
     * Метод аутентификации игрока для выдачи jwt-токена
     *
     * @param playerRequestDto информация об игроке
     * @return ответ на HTTP-запрос
     */
    @ApiOperation("Метод для авторизации игрока")
    @PostMapping(value = "/authorization", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseJwtToken> authorization(@RequestBody PlayerRequestDto playerRequestDto) {
        PlayerResponseDto playerResponseDto = playerService.authorization(playerRequestDto);
        String jwtToken = getJwtToken(playerResponseDto);
        return ResponseEntity.ok(new ResponseJwtToken(jwtToken));
    }

    /**
     * Метод для создания транзакции игрока
     *
     * @param transactionRequestDto информация об транзакции
     * @param jwtToken              jwt-токен взят из заголовка запроса
     * @return ответ на HTTP-запрос
     */
    @ApiOperation("Метод для создания транзакций игрока")
    @PostMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlayerResponseDto> createTransaction(@RequestBody TransactionRequestDto transactionRequestDto,
                                                               @RequestHeader("Authorization") String jwtToken) {
        checkTransactionDto(transactionRequestDto);
        String loginPlayer = getLoginPlayerByJwtToken(jwtToken);
        PlayerResponseDto playerResponseDto =
                transactionRequestDto.type().equals(TransactionType.CREDIT) ?
                        playerService.creditForPlayer(loginPlayer, transactionRequestDto) :
                        playerService.debitForPlayer(loginPlayer, transactionRequestDto);
        return ResponseEntity.ok(playerResponseDto);
    }

    /**
     * Метод для просмотра истории транзакции типа CREDIT или DEBIT
     *
     * @param jwtToken               jwt-токен взят из заголовка запроса
     * @param typeHistoryTransaction параметр переданный
     * @return ответ на HTTP-запрос
     */
    @ApiOperation("Метод для получения истории по транзакциям игрока")
    @GetMapping(value = "/transactions/{typeHistoryTransaction}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionResponseDto>> getHistoryTransactions(
            @RequestHeader("Authorization") String jwtToken, @PathVariable String typeHistoryTransaction) {
        Long playerId = getPlayerIdByJwtToken(jwtToken);
        List<TransactionResponseDto> transactionsResponseDto;

        if (typeHistoryTransaction.toUpperCase().equals(TransactionType.CREDIT.toString())) {
            transactionsResponseDto = transactionService.findHistoryTransactions(playerId, TransactionType.CREDIT);
        } else if (typeHistoryTransaction.toUpperCase().equals(TransactionType.DEBIT.toString())) {
            transactionsResponseDto = transactionService.findHistoryTransactions(playerId, TransactionType.DEBIT);
        } else {
            throw new InvalidInputException(String
                    .format("Такого типа транзакции(%s) не существует", typeHistoryTransaction));
        }

        return ResponseEntity.ok(transactionsResponseDto);
    }

    /**
     * Метод для просмотра аудитов игрока
     *
     * @param jwtToken jwt-токен взят из заголовка запроса
     * @return ответ на HTTP-запрос
     */
    @ApiOperation("Метод для получения аудитов игрока")
    @GetMapping(value = "/audits", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditResponseDto>> getAudits(@RequestHeader("Authorization") String jwtToken) {
        Long playerId = getPlayerIdByJwtToken(jwtToken);
        List<AuditResponseDto> auditsResponseDto =
                auditService.findAuditsByLoginPlayer(playerId);
        return ResponseEntity.ok(auditsResponseDto);
    }

    /**
     * Метод для генерации jwt-токена с темой authorization, с полезной нагрузкой и ограничением
     * по времени существования
     *
     * @param playerResponseDto dto объект для добавление в payload, при генерации jwt-токена
     * @return сгенерированный jwt-токен
     */
    private static String getJwtToken(PlayerResponseDto playerResponseDto) {
        return Jwts.builder()
                .setSubject("authorization")
                .claim("id", playerResponseDto.id())
                .claim("login", playerResponseDto.login())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtUtil.oneHourInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, JwtUtil.secret)
                .compact();
    }

    /**
     * Метод для валидации данных дто объекта
     *
     * @param transactionRequestDto дто объект транзакции
     * @throws InvalidInputException если объект не прошел проверку
     */
    private void checkTransactionDto(TransactionRequestDto transactionRequestDto) {
        if (transactionRequestDto.id() == null ||
                transactionRequestDto.size() == null ||
                transactionRequestDto.type() == null) {
            throw new InvalidInputException("Один или несколько значений транзакций пустые.");
        }
        if (transactionRequestDto.size() < 0) {
            throw new InvalidInputException("Размер транзакции не должен быть нулем.");
        }
    }

    /**
     * Метод для получения логина игрока из jwt-токена
     *
     * @param jwtToken jwt-токен игрока при запросе
     * @return логин игрока
     */
    private String getLoginPlayerByJwtToken(String jwtToken) {
        return Jwts.parser()
                .setSigningKey(JwtUtil.secret)
                .parseClaimsJws(jwtToken)
                .getBody().get("login", String.class);
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
