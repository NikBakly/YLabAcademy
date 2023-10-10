package org.example.service;

import org.example.model.Transaction;
import org.example.repository.TransactionInMemoryRepository;
import org.example.util.TransactionType;
import org.junit.jupiter.api.*;

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
        transactionService = new TransactionService(new TransactionInMemoryRepository());
    }

    /**
     * Тест для проверки шаблона проектирования Singleton
     */
    @Test
    @DisplayName("Тест 1. Проверка шаблона проектирования Singleton.")
    void getInstance() {
        TransactionService firstPointer = TransactionService.getInstance();
        TransactionService secondPointer = TransactionService.getInstance();
        Assertions.assertSame(firstPointer, secondPointer, "Указатели ссылаются на разные объекты.");
    }

    /**
     * Тест для проверки создания debit транзакции и просмотра ее истории
     */
    @Test
    @DisplayName("Тест 2. Удачное создание debit транзакции и просмотр ее в историях")
    void createAndGetHistoryDebitTransactions() {
        transactionService.createTransaction(transactionId, debitTransactionType, transactionSize, loginPlayer);
        List<Transaction> foundTransactions = transactionService.getDebitHistoryTransactions(loginPlayer);

        int expectedSizeList = 1;
        Assertions.assertEquals(expectedSizeList, foundTransactions.size(),
                "Ожидаемый размер списка не совпадает с результатом");
        Transaction foundTransaction = foundTransactions.get(0);
        Assertions.assertTrue(
                foundTransaction.loginPlayer().equals(loginPlayer) &&
                        foundTransaction.type().equals(debitTransactionType) &&
                        foundTransaction.id().equals(transactionId) &&
                        foundTransaction.size().equals(transactionSize),
                "Найденная транзакция не совпадает с ожиданиями");

    }

    /**
     * Тест для проверки создания credit транзакции и просмотра ее истории
     */
    @Test
    @DisplayName("Тест 2. Удачное создание credit транзакции и просмотр ее в историях")
    void createAndGetHistoryCreditTransactions() {
        transactionService.createTransaction(transactionId, creditTransactionType, transactionSize, loginPlayer);
        List<Transaction> foundTransactions = transactionService.getCreditHistoryTransactions(loginPlayer);

        int expectedSizeList = 1;
        Assertions.assertEquals(expectedSizeList, foundTransactions.size(),
                "Ожидаемый размер списка не совпадает с результатом");
        Transaction foundTransaction = foundTransactions.get(0);
        Assertions.assertTrue(
                foundTransaction.loginPlayer().equals(loginPlayer) &&
                        foundTransaction.type().equals(creditTransactionType) &&
                        foundTransaction.id().equals(transactionId) &&
                        foundTransaction.size().equals(transactionSize),
                "Найденная транзакция не совпадает с ожиданиями");

    }


}