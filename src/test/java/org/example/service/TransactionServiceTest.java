package org.example.service;

import org.assertj.core.api.Assertions;
import org.example.model.Transaction;
import org.example.repository.TransactionInMemoryRepository;
import org.example.util.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Класс для тестирования TransactionService
 */
class TransactionServiceTest {
    static long transactionId;
    static String loginPlayer;
    static TransactionType creditTransactionType;
    static TransactionType debitTransactionType;
    static double transactionSize;
    TransactionService transactionService;

    @BeforeAll
    static void init() {
        transactionId = 1;
        loginPlayer = "tester";
        creditTransactionType = TransactionType.CREDIT;
        debitTransactionType = TransactionType.DEBIT;
        transactionSize = 1000;
    }

    @BeforeEach
    void setTransactionService() {
        transactionService = new TransactionServiceImpl(new TransactionInMemoryRepository());
    }

    /**
     * Тест для проверки шаблона проектирования Singleton
     */
    @Test
    @DisplayName("Проверка шаблона проектирования Singleton.")
    void getInstance() {
        TransactionServiceImpl firstPointer = TransactionServiceImpl.getInstance();
        TransactionServiceImpl secondPointer = TransactionServiceImpl.getInstance();
        Assertions.assertThat(firstPointer)
                .as("Указатели ссылаются на разные объекты.")
                .isEqualTo(secondPointer);
    }

    /**
     * Тест для проверки создания debit транзакции и просмотра ее истории
     */
    @Test
    @DisplayName("Удачное создание debit транзакции и просмотр ее в историях")
    void createAndGetHistoryDebitTransactions() {
        transactionService.createTransaction(transactionId, debitTransactionType, transactionSize, loginPlayer);
        List<Transaction> foundTransactions = transactionService.getDebitHistoryTransactions(loginPlayer);

        int expectedSizeList = 1;
        Assertions.assertThat(expectedSizeList)
                .as("Ожидаемый размер списка не совпадает с результатом")
                .isEqualTo(foundTransactions.size());

        Transaction foundTransaction = foundTransactions.get(0);
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
    void createAndGetHistoryCreditTransactions() {
        transactionService.createTransaction(transactionId, creditTransactionType, transactionSize, loginPlayer);
        List<Transaction> foundTransactions = transactionService.getCreditHistoryTransactions(loginPlayer);

        int expectedSizeList = 1;
        Assertions.assertThat(expectedSizeList)
                .as("Ожидаемый размер списка не совпадает с результатом")
                .isEqualTo(foundTransactions.size());

        Transaction foundTransaction = foundTransactions.get(0);
        Assertions.assertThat(foundTransaction.loginPlayer().equals(loginPlayer) &&
                        foundTransaction.type().equals(creditTransactionType) &&
                        foundTransaction.id().equals(transactionId) &&
                        foundTransaction.size().equals(transactionSize))
                .as("Найденная транзакция не совпадает с ожиданиями")
                .isTrue();
    }
}