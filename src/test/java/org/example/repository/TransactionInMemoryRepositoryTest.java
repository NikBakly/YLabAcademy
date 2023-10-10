package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.util.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Класс для тестирования TransactionInMemoryRepository
 */
class TransactionInMemoryRepositoryTest {
    static TransactionInMemoryRepository repository;
    Double sizeTransaction = 120.0;
    String loginPlayer = "tester";


    @BeforeAll
    static void init() {
        repository = TransactionInMemoryRepository.getInstance();
    }

    /**
     * Тестирование шаблона проектирования Singleton
     */
    @Test
    @DisplayName("Тест 1. Проверка шаблона проектирования Singleton.")
    void getInstance() {
        TransactionInMemoryRepository secondPointer = TransactionInMemoryRepository.getInstance();
        Assertions.assertSame(repository, secondPointer, "Указатели ссылаются на разные объекты.");
    }

    /**
     * Тестирование создания транзакций типа CREDIT и показ истории таких транзакций
     */
    @Test
    @DisplayName("Тест 2. Создание транзакции типа CREDIT и нахождения ее по логину пользователя.")
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
    @DisplayName("Тест 3. Создание транзакции типа DEBIT и нахождения ее по логину пользователя.")
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
    @DisplayName("Тест 4. Создание транзакции с не уникальным id.")
    void createdDebitTransactionWhenTransactionSizeIsLargerThanPlayerPersonalFund() {
        Long repeatingId = 3L;
        repository.createdTransaction(repeatingId, TransactionType.CREDIT, sizeTransaction, loginPlayer);
        SaveEntityException exception = Assertions.assertThrows(SaveEntityException.class, () ->
                repository.createdTransaction(repeatingId, TransactionType.CREDIT, sizeTransaction, loginPlayer));
        String expectedErrorMessage = "Id транзакции не является уникальным!";
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

}