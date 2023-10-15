package org.example.repository;

import org.assertj.core.api.Assertions;
import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.util.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Класс для тестирования TransactionInMemoryRepository
 */
class TransactionInMemoryRepositoryTest {
    static TransactionRepository repository;
    static Double sizeTransaction;
    static String loginPlayer;
    static Long transactionId;

    @BeforeAll
    static void init() {
        sizeTransaction = 120.0;
        loginPlayer = "tester";
        transactionId = 1L;
    }

    @BeforeEach
    void initRepository() {
        repository = new TransactionInMemoryRepository();
    }

    /**
     * Тестирование создания транзакций типа CREDIT и показ истории таких транзакций
     */
    @Test
    @DisplayName("Удачное создание транзакции типа CREDIT и нахождения ее по логину пользователя.")
    void createdTransactionAndFindCreditHistoryTransactions() {
        TransactionType transactionType = TransactionType.CREDIT;
        repository.createdTransaction(transactionId, transactionType, sizeTransaction, loginPlayer);

        //Достаем единственную транзакцию из истории пользователя
        Transaction foundTransaction = repository.findCreditHistoryTransactionsByCreatedTime(loginPlayer).get(0);
        Assertions.assertThat(foundTransaction.loginPlayer().equals(loginPlayer)
                        && foundTransaction.type().equals(transactionType))
                .as("Транзакция не правильно создана.")
                .isTrue();
    }

    /**
     * Тестирование создания транзакций типа DEBIT и показ истории таких транзакций
     */
    @Test
    @DisplayName("Удачное создание транзакции типа DEBIT и нахождения ее по логину пользователя.")
    void createdTransactionAndFindDebitHistoryTransactions() {
        TransactionType transactionType = TransactionType.DEBIT;
        repository.createdTransaction(transactionId, transactionType, sizeTransaction, loginPlayer);
        //Достаем единственную транзакцию из истории пользователя
        Transaction foundTransaction = repository.findDebitHistoryTransactionsByCreatedTime(loginPlayer).get(0);
        Assertions.assertThat(foundTransaction.loginPlayer().equals(loginPlayer)
                        && foundTransaction.type().equals(transactionType))
                .as("Транзакция не правильно создана.")
                .isTrue();
    }

    /**
     * Тестирование создания транзакции с не уникальном id
     */
    @Test
    @DisplayName("Не удачное создание транзакции с не уникальным id.")
    void createdDebitTransactionWhenTransactionSizeIsLargerThanPlayerPersonalFund() {
        repository.createdTransaction(transactionId, TransactionType.CREDIT, sizeTransaction, loginPlayer);
        Throwable thrown = Assertions.catchThrowable(() ->
                repository.createdTransaction(transactionId, TransactionType.CREDIT, sizeTransaction, loginPlayer));
        String expectedErrorMessage = "Id транзакции не является уникальным!";
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(SaveEntityException.class)
                .hasMessage(expectedErrorMessage);
    }
}