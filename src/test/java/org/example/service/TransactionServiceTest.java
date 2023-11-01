package org.example.service;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.TransactionResponseDto;
import org.example.domain.model.Transaction;
import org.example.repository.TransactionRepository;
import org.example.util.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Класс для тестирования TransactionService
 */
class TransactionServiceTest {
    static long transactionId;
    static long playerId;
    static String loginPlayer;
    static TransactionType creditTransactionType;
    static TransactionType debitTransactionType;
    static double transactionSize;
    static TransactionService transactionService;

    @BeforeAll
    static void init() {
        transactionId = 1;
        playerId = 1;
        loginPlayer = "tester";
        creditTransactionType = TransactionType.CREDIT;
        debitTransactionType = TransactionType.DEBIT;
        transactionSize = 1000;

        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        transactionService = new TransactionServiceImpl(transactionRepository);

        when(transactionRepository.findHistoryTransactionsByCreatedTime(playerId, TransactionType.DEBIT))
                .thenReturn(List.of(new TransactionResponseDto(
                        transactionId,
                        debitTransactionType,
                        transactionSize,
                        loginPlayer,
                        Instant.now())
                ));
        when(transactionRepository.findHistoryTransactionsByCreatedTime(playerId, TransactionType.CREDIT))
                .thenReturn(List.of(new TransactionResponseDto(
                        transactionId,
                        creditTransactionType,
                        transactionSize,
                        loginPlayer,
                        Instant.now())
                ));

        //Отключение аспектов
        System.setProperty("disableAspect", "true");
    }

    /**
     * Тест для проверки создания debit транзакции и просмотра ее истории
     */
    @Test
    @DisplayName("Удачное создание debit транзакции и просмотр ее в историях")
    void testCreateAndGetHistoryDebitTransactions() {
        transactionService.createTransaction(
                new Transaction(transactionId, debitTransactionType, transactionSize, playerId, Instant.now()));
        List<TransactionResponseDto> foundTransactions =
                transactionService.findHistoryTransactions(playerId, TransactionType.DEBIT);

        int expectedSizeList = 1;
        Assertions.assertThat(expectedSizeList)
                .as("Ожидаемый размер списка не совпадает с результатом")
                .isEqualTo(foundTransactions.size());

        TransactionResponseDto foundTransaction = foundTransactions.get(0);
        Assertions.assertThat(foundTransaction.loginPlayer().equals(loginPlayer) &&
                        foundTransaction.type().equals(debitTransactionType) &&
                        foundTransaction.id().equals(transactionId) &&
                        foundTransaction.size().equals(transactionSize))
                .as("Найденная транзакция не совпадает с ожиданиями")
                .isTrue();
    }

    /**
     * Тест для проверки создания credit транзакции и просмотра ее истории
     */
    @Test
    @DisplayName("Удачное создание credit транзакции и просмотр ее в историях")
    void testCreateAndGetHistoryCreditTransactions() {
        transactionService.createTransaction(
                new Transaction(transactionId, creditTransactionType, transactionSize, playerId, Instant.now()));
        List<TransactionResponseDto> foundTransactions =
                transactionService.findHistoryTransactions(playerId, TransactionType.CREDIT);

        int expectedSizeList = 1;
        Assertions.assertThat(expectedSizeList)
                .as("Ожидаемый размер списка не совпадает с результатом")
                .isEqualTo(foundTransactions.size());

        TransactionResponseDto foundTransaction = foundTransactions.get(0);
        Assertions.assertThat(foundTransaction.loginPlayer().equals(loginPlayer) &&
                        foundTransaction.type().equals(creditTransactionType) &&
                        foundTransaction.id().equals(transactionId) &&
                        foundTransaction.size().equals(transactionSize))
                .as("Найденная транзакция не совпадает с ожиданиями")
                .isTrue();
    }
}