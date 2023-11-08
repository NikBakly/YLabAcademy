package org.example.repository;

import org.assertj.core.api.Assertions;
import org.example.domain.model.Player;
import org.example.exception.SaveEntityException;
import org.example.util.DatabaseConnector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Класс для тестирования PlayerInMemoryRepository
 */
@Testcontainers
@SpringBootTest
class PlayerRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName(DatabaseConnector.DATABASE_NAME)
            .withUsername(DatabaseConnector.USERNAME)
            .withPassword(DatabaseConnector.PASSWORD);

    @Autowired
    PlayerRepository repository;

    @DynamicPropertySource
    static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    static void init() {
        postgresContainer.start();
    }

    @AfterAll
    static void closeContainer() {
        postgresContainer.close();
    }

    /**
     * Тестирование создания и нахождения игрока по его логину
     */
    @Test
    @DisplayName("Удачное создание и нахождения игрока по его логину")
    void testSaveAndFindPlayerByLogin() {
        String expectedLoginPlayer = "test1";
        String expectedPasswordPlayer = "pass";
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
        String expectedLoginPlayer = "test2";
        String expectedPasswordPlayer = "pass";
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
        String expectedLoginPlayer = "test3";
        String expectedPasswordPlayer = "pass";
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
        String expectedLoginPlayer = "random";
        Optional<Player> foundPlayer = repository.findByLogin(expectedLoginPlayer);
        Assertions.assertThat(foundPlayer.isEmpty())
                .as("Найденная сущность должна быть пустым")
                .isTrue();
    }
}