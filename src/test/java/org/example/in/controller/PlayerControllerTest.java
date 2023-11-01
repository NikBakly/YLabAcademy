package org.example.in.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.Assertions;
import org.example.domain.dto.*;
import org.example.exception.InvalidInputException;
import org.example.service.AuditService;
import org.example.service.PlayerService;
import org.example.service.TransactionService;
import org.example.util.JwtUtil;
import org.example.util.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

class PlayerControllerTest {
    private static final Long playerId = 1L;
    private static final String loginPlayer = "login";
    private static final String passwordPlayer = "password";
    private static final Double balancePlayer = 0.0;

    static String jwtToken;

    @InjectMocks
    private PlayerController playerController;

    @Mock
    private PlayerService playerService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private AuditService auditService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerController = new PlayerController(playerService, transactionService, auditService);
    }

    @BeforeAll
    static void init() {
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
    @DisplayName("Успешная регистрация игрока")
    void testRegistrationPlayer() {
        PlayerRequestDto newPlayer = new PlayerRequestDto(loginPlayer, passwordPlayer);
        PlayerResponseDto expectedPlayer = new PlayerResponseDto(playerId, loginPlayer, BigDecimal.valueOf(balancePlayer));
        when(playerService.registration(newPlayer)).thenReturn(expectedPlayer);

        ResponseEntity<PlayerResponseDto> response = playerController.registration(newPlayer);

        Mockito.verify(playerService).registration(newPlayer);
        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isEqualTo(expectedPlayer);
    }

    @Test
    @DisplayName("Успешная авторизация игрока")
    void testAuthorizationPlayer() {
        PlayerRequestDto playerRequestDto = new PlayerRequestDto(loginPlayer, passwordPlayer);
        PlayerResponseDto expectedPlayer = new PlayerResponseDto(playerId, loginPlayer, BigDecimal.valueOf(balancePlayer));
        when(playerService.authorization(playerRequestDto)).thenReturn(expectedPlayer);

        ResponseEntity<Map<String, String>> response = playerController.authorization(playerRequestDto);

        Mockito.verify(playerService).authorization(playerRequestDto);
        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        String actualJwtToken = response.getBody().get("jwt token");
        Claims claims = Jwts.parser()
                .setSigningKey(JwtUtil.secret)
                .parseClaimsJws(actualJwtToken)
                .getBody();

        Assertions.assertThat(claims.get("id", Long.class)).isEqualTo(playerId);
        Assertions.assertThat(claims.get("login", String.class)).isEqualTo(loginPlayer);
    }

    @Test
    @DisplayName("Успешная создания транзакции типа кредит")
    void testCreateCreditTransaction() {
        double transactionSize = 1200;
        TransactionRequestDto transactionRequestDto =
                new TransactionRequestDto(1L, TransactionType.CREDIT, transactionSize);

        PlayerResponseDto expectedPlayer =
                new PlayerResponseDto(playerId, loginPlayer, BigDecimal.valueOf(balancePlayer + transactionSize));
        when(playerService.creditForPlayer(loginPlayer, transactionRequestDto)).thenReturn(expectedPlayer);

        ResponseEntity<PlayerResponseDto> response = playerController.createTransaction(transactionRequestDto, jwtToken);

        Mockito.verify(playerService).creditForPlayer(loginPlayer, transactionRequestDto);
        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(response.getBody()).isEqualTo(expectedPlayer);
    }

    @Test
    @DisplayName("Успешная создания транзакции типа дебит")
    void testCreateDebitTransaction() {
        TransactionRequestDto transactionRequestDto =
                new TransactionRequestDto(1L, TransactionType.DEBIT, 0.0);

        PlayerResponseDto expectedPlayer =
                new PlayerResponseDto(playerId, loginPlayer, BigDecimal.valueOf(balancePlayer));
        when(playerService.debitForPlayer(loginPlayer, transactionRequestDto)).thenReturn(expectedPlayer);

        ResponseEntity<PlayerResponseDto> response = playerController.createTransaction(transactionRequestDto, jwtToken);

        Mockito.verify(playerService).debitForPlayer(loginPlayer, transactionRequestDto);
        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(response.getBody()).isEqualTo(expectedPlayer);
    }

    @Test
    @DisplayName("Не успешное создание транзакции типа кредит, когда размер транзакции меньше нуля")
    void testCreateCreditTransactionWhenTransactionSizeLessThanZero() {
        double transactionSize = -1200;
        TransactionRequestDto transactionRequestDto =
                new TransactionRequestDto(1L, TransactionType.CREDIT, transactionSize);

        Throwable thrown = Assertions.catchThrowable(() ->
                playerController.createTransaction(transactionRequestDto, jwtToken));

        Assertions.assertThat(thrown)
                .as("Должно было быть исключение")
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    @DisplayName("Не успешное создание транзакции типа кредит, когда размер транзакции меньше нуля")
    void testCreateCreditTransactionWhenTransactionTypeIsNull() {
        double transactionSize = 1200;
        TransactionRequestDto transactionRequestDto =
                new TransactionRequestDto(1L, null, transactionSize);

        Throwable thrown = Assertions.catchThrowable(() ->
                playerController.createTransaction(transactionRequestDto, jwtToken));

        Assertions.assertThat(thrown)
                .as("Должно было быть исключение")
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    @DisplayName("Успешная получение истории транзакций типа кредит")
    void getHistoryCreditTransactions() {
        when(transactionService.findHistoryTransactions(playerId, TransactionType.CREDIT)).thenReturn(List.of());
        ResponseEntity<List<TransactionResponseDto>> response =
                playerController.getHistoryTransactions(jwtToken, TransactionType.CREDIT.name());

        Mockito.verify(transactionService).findHistoryTransactions(playerId, TransactionType.CREDIT);
        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Успешная получение истории транзакций типа кредит")
    void getHistoryDebitTransactions() {
        when(transactionService.findHistoryTransactions(playerId, TransactionType.DEBIT)).thenReturn(List.of());
        ResponseEntity<List<TransactionResponseDto>> response =
                playerController.getHistoryTransactions(jwtToken, TransactionType.DEBIT.name());

        Mockito.verify(transactionService).findHistoryTransactions(playerId, TransactionType.DEBIT);
        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Не успешное получение истории транзакций из-за не определенного типа")
    void getHistoryTransactionsWhenTransactionTypeInNotFound() {
        Throwable thrown = Assertions.catchThrowable(() ->
                playerController.getHistoryTransactions(jwtToken, "batman"));

        Assertions.assertThat(thrown)
                .as("Должно было быть исключение")
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    @DisplayName("Успешная получение аудитов игрока")
    void getAudits() {
        when(auditService.findAuditsByLoginPlayer(playerId)).thenReturn(List.of());
        ResponseEntity<List<AuditResponseDto>> response =
                playerController.getAudits(jwtToken);

        Mockito.verify(auditService).findAuditsByLoginPlayer(playerId);
        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

}