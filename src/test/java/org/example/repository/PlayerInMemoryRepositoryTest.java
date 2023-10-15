package org.example.repository;

import org.assertj.core.api.Assertions;
import org.example.exception.SaveEntityException;
import org.example.model.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Класс для тестирования PlayerInMemoryRepository
 */
class PlayerInMemoryRepositoryTest {
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
        repository = new PlayerInMemoryRepository();
    }

    /**
     * Тестирование создания и нахождения игрока по его логину
     */
    @Test
    @DisplayName("Удачное создание и нахождения игрока по его логину")
    void saveAndFindPlayerByLogin() {
        repository.save(expectedLoginPlayer, expectedPasswordPlayer);
        Optional<Player> foundPlayer = repository.findByLogin(expectedLoginPlayer);
        if (foundPlayer.isPresent()) {
            Assertions.assertThat(expectedLoginPlayer)
                    .as("Логины не равны.")
                    .isEqualTo(foundPlayer.get().getLogin());
            Assertions.assertThat(expectedPasswordPlayer)
                    .as("Пароли не равны.")
                    .isEqualTo(foundPlayer.get().getPassword());
        } else {
            Assertions.fail("Сущность не найдена");
        }
    }

    /**
     * Тестирование создания игрока с не уникальным логином
     */
    @Test
    @DisplayName("Не удачное создания игрока с не уникальным логином")
    void savePlayerWhenLoginNotUnique() {
        repository.save(expectedLoginPlayer, expectedPasswordPlayer);
        Throwable thrown = Assertions.catchThrowable(() ->
                repository.save(expectedLoginPlayer, expectedPasswordPlayer));
        String expectedErrorMessage = "Игрок с таким логином существует!";
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(SaveEntityException.class)
                .hasMessage(expectedErrorMessage);
    }
}