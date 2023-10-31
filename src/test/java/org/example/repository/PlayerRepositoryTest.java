package org.example.repository;

import org.assertj.core.api.Assertions;
import org.example.domain.model.Player;
import org.example.exception.SaveEntityException;
import org.example.util.DatabaseConnector;
import org.example.util.LiquibaseManager;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Класс для тестирования PlayerInMemoryRepository
 */
@Testcontainers
class PlayerRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName(DatabaseConnector.DATABASE_NAME)
            .withUsername(DatabaseConnector.USERNAME)
            .withPassword(DatabaseConnector.PASSWORD);

    PlayerRepository repository;
    static String expectedLoginPlayer;
    static String expectedPasswordPlayer;

    @BeforeAll
    static void init() {
        expectedLoginPlayer = "test";
        expectedPasswordPlayer = "test";
    }


    @BeforeEach
    void initRepository() {
        postgresContainer.start();
        LiquibaseManager.runDatabaseMigrations(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());

        repository = new PlayerRepositoryImpl(
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
     * Тестирование создания и нахождения игрока по его логину
     */
    @Test
    @DisplayName("Удачное создание и нахождения игрока по его логину")
    void testSaveAndFindPlayerByLogin() {
        repository.save(expectedLoginPlayer, expectedPasswordPlayer);
        Player foundPlayer = repository.findByLogin(expectedLoginPlayer).get();
        Assertions.assertThat(expectedLoginPlayer)
                .as("Логины не равны.")
                .isEqualTo(foundPlayer.getLogin());
        Assertions.assertThat(expectedPasswordPlayer)
                .as("Пароли не равны.")
                .isEqualTo(foundPlayer.getPassword());
        BigDecimal expectedBalance = BigDecimal.valueOf(0);
        int resultComparing = expectedBalance.compareTo(foundPlayer.getBalance());
        Assertions.assertThat(resultComparing == 0)
                .as("Балансы не равные.")
                .isTrue();
    }

    /**
     * Тестирование создания игрока с не уникальным логином
     */
    @Test
    @DisplayName("Не удачное создания игрока с не уникальным логином")
    void testSavePlayerWhenLoginNotUnique() {
        repository.save(expectedLoginPlayer, expectedPasswordPlayer);
        Throwable thrown = Assertions.catchThrowable(() ->
                repository.save(expectedLoginPlayer, expectedPasswordPlayer));
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(SaveEntityException.class);
    }

    @Test
    @DisplayName("Удачное обновление баланса у пользователя")
    void testUpdateBalanceByLogin() {
        repository.save(expectedLoginPlayer, expectedPasswordPlayer);
        Optional<Player> foundPlayer = repository.findByLogin(expectedLoginPlayer);
        if (foundPlayer.isPresent()) {
            Player player = foundPlayer.get();
            BigDecimal olbBalance = player.getBalance();
            BigDecimal newBalance = olbBalance.add(BigDecimal.valueOf(100));
            repository.updateBalanceByLogin(expectedLoginPlayer, newBalance.doubleValue());
            foundPlayer = repository.findByLogin(expectedLoginPlayer);
            if (foundPlayer.isPresent()) {
                Assertions.assertThat(newBalance.doubleValue())
                        .as("Полученный баланс игрока не совпадает с реальным")
                        .isEqualTo(foundPlayer.get().getBalance().doubleValue());
            } else {
                Assertions.fail("Игрок не найден");
            }

        } else {
            Assertions.fail("Игрок не найден");
        }
    }

    @Test
    @DisplayName("Не удачное нахождение игрока из-за неверного логина")
    void testFindPlayerByLogin() {
        Optional<Player> foundPlayer = repository.findByLogin(expectedLoginPlayer);
        Assertions.assertThat(foundPlayer.isEmpty())
                .as("Найденная сущность должна быть пустым")
                .isTrue();
    }
}