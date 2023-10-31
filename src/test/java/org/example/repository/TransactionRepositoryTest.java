package org.example.repository;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.TransactionResponseDto;
import org.example.domain.model.Transaction;
import org.example.exception.SaveEntityException;
import org.example.util.DatabaseConnector;
import org.example.util.LiquibaseManager;
import org.example.util.TransactionType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

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
    static PlayerRepository playerRepository;
    static Double sizeTransaction;
    static String loginPlayer;
    static String passwordPlayer;
    static Long playerId;
    static Long transactionId;

    @BeforeAll
    static void init() {
        playerId = 3L;
        sizeTransaction = 120.0;
        loginPlayer = "tester";
        passwordPlayer = "passTest";
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
        playerRepository = new PlayerRepositoryImpl(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );
    }

    @AfterEach
    void closeContainer() {
        postgresContainer.close();
    }

    /**
     * Тестирование создания транзакций типа CREDIT и показ истории таких транзакций
     */
    @Test
    @DisplayName("Удачное создание транзакции типа CREDIT и нахождения ее по идентификатору игрока.")
    void testCreatedTransactionAndFindCreditHistoryTransactions() {
        playerRepository.save(loginPlayer, passwordPlayer);
        TransactionType transactionType = TransactionType.CREDIT;
        repository.createdTransaction(
                new Transaction(transactionId, transactionType, sizeTransaction, playerId, Instant.now()));

        //Достаем единственную транзакцию из истории пользователя
        TransactionResponseDto foundTransaction =
                repository.findHistoryTransactionsByCreatedTime(playerId, transactionType).get(0);

        Assertions.assertThat(foundTransaction.loginPlayer().equals(loginPlayer)
                        && foundTransaction.type().equals(transactionType))
                .as("Транзакция не правильно создана.")
                .isTrue();
    }

    /**
     * Тестирование создания транзакций типа DEBIT и показ истории таких транзакций
     */
    @Test
    @DisplayName("Удачное создание транзакции типа DEBIT и нахождения ее по идентификатору игрока.")
    void testCreatedTransactionAndFindDebitHistoryTransactions() {
        playerRepository.save(loginPlayer, passwordPlayer);
        TransactionType transactionType = TransactionType.DEBIT;
        repository.createdTransaction(
                new Transaction(transactionId, transactionType, sizeTransaction, playerId, Instant.now()));
        //Достаем единственную транзакцию из истории пользователя
        TransactionResponseDto foundTransaction =
                repository.findHistoryTransactionsByCreatedTime(playerId, transactionType).get(0);
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
    void testCreatedDebitTransactionWhenTransactionSizeIsLargerThanPlayerPersonalFund() {
        playerRepository.save(loginPlayer, passwordPlayer);
        repository.createdTransaction(
                new Transaction(transactionId, TransactionType.CREDIT, sizeTransaction, playerId, Instant.now()));
        Throwable thrown = Assertions.catchThrowable(() ->
                repository.createdTransaction(
                        new Transaction(transactionId, TransactionType.CREDIT, sizeTransaction, playerId, Instant.now())
                ));
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(SaveEntityException.class);
    }
}