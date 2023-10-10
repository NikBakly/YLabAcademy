package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.util.TransactionType;
import org.junit.jupiter.api.*;

/**
 * Класс для тестирования TransactionInMemoryRepository
 */
class TransactionInMemoryRepositoryTest {
    static TransactionInMemoryRepository repository;
    static Double sizeTransaction;
    static String loginPlayer;

    @BeforeAll
    static void init() {
        sizeTransaction = 120.0;
        loginPlayer = "tester";
    }

    @BeforeEach
    void initRepository() {
        repository = new TransactionInMemoryRepository();
    }

    /**
     * Тестирование создания транзакций типа CREDIT и показ истории таких транзакций
     */
    @Test
    @DisplayName("Тест 1. Удачное создание транзакции типа CREDIT и нахождения ее по логину пользователя.")
    void createdTransactionAndFindCreditHistoryTransactions() {
        Long uniqueTransactionId = 1L;
        TransactionType transactionType = TransactionType.CREDIT;
        repository.createdTransaction(uniqueTransactionId, transactionType, sizeTransaction, loginPlayer);

        //Достаем единственную транзакцию из истории пользователя
        Transaction foundTransaction = repository.findCreditHistoryTransactionsByCreatedTime(loginPlayer).get(0);
        Assertions.assertTrue(
                foundTransaction.loginPlayer().equals(loginPlayer)
                        && foundTransaction.type().equals(transactionType),
                "Транзакция не правильно создана");
    }

    /**
     * Тестирование создания транзакций типа DEBIT и показ истории таких транзакций
     */
    @Test
    @DisplayName("Тест 2. Удачное создание транзакции типа DEBIT и нахождения ее по логину пользователя.")
    void createdTransactionAndFindDebitHistoryTransactions() {
        Long uniqueTransactionId = 2L;
        TransactionType transactionType = TransactionType.DEBIT;
        repository.createdTransaction(uniqueTransactionId, transactionType, sizeTransaction, loginPlayer);
        //Достаем единственную транзакцию из истории пользователя
        Transaction foundTransaction = repository.findDebitHistoryTransactionsByCreatedTime(loginPlayer).get(0);
        Assertions.assertTrue(
                foundTransaction.loginPlayer().equals(loginPlayer)
                        && foundTransaction.type().equals(transactionType),
                "Транзакция не правильно создана");
    }

    /**
     * Тестирование создания транзакции с не уникальном id
     */
    @Test
    @DisplayName("Тест 3. Не удачное создание транзакции с не уникальным id.")
    void createdDebitTransactionWhenTransactionSizeIsLargerThanPlayerPersonalFund() {
        Long repeatingId = 3L;
        repository.createdTransaction(repeatingId, TransactionType.CREDIT, sizeTransaction, loginPlayer);
        SaveEntityException exception = Assertions.assertThrows(SaveEntityException.class, () ->
                repository.createdTransaction(repeatingId, TransactionType.CREDIT, sizeTransaction, loginPlayer));
        String expectedErrorMessage = "Id транзакции не является уникальным!";
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }
}