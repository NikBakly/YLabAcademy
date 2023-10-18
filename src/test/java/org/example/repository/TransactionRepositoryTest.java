package org.example.repository;

import org.assertj.core.api.Assertions;
import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.util.DatabaseConnector;
import org.example.util.LiquibaseManager;
import org.example.util.TransactionType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Класс для тестирования TransactionInMemoryRepository
 */
@Testcontainers
class TransactionRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName(DatabaseConnector.DATABASE_NAME)
            .withUsername(DatabaseConnector.USERNAME)
            .withPassword(DatabaseConnector.PASSWORD);

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
        postgresContainer.start();
        LiquibaseManager.runDatabaseMigrations(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
        repository = new TransactionRepositoryImpl(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
    }

    @AfterEach
    void closeContainer() {
        postgresContainer.close();
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
        Transaction foundTransaction =
                repository.findHistoryTransactionsByCreatedTime(loginPlayer, TransactionType.CREDIT).get(0);
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
        Transaction foundTransaction =
                repository.findHistoryTransactionsByCreatedTime(loginPlayer, TransactionType.DEBIT).get(0);
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
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(SaveEntityException.class);
    }
}