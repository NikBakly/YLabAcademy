package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Player;
import org.junit.jupiter.api.*;

/**
 * Класс для тестирования PlayerInMemoryRepository
 */
class PlayerInMemoryRepositoryTest {
    PlayerInMemoryRepository repository;
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
    @DisplayName("Тест 1. Удачное создание и нахождения игрока по его логину")
    void saveAndFindPlayerByLogin() {
        repository.save(expectedLoginPlayer, expectedPasswordPlayer);
        Player foundPlayer = repository.findByLogin(expectedLoginPlayer);
        Assertions.assertEquals(expectedLoginPlayer, foundPlayer.getLogin(), "Логины не равны");
        Assertions.assertEquals(expectedPasswordPlayer, foundPlayer.getPassword(), "Логины не равны");
    }

    /**
     * Тестирование создания игрока с не уникальным логином
     */
    @Test
    @DisplayName("Тест 2. Не удачное создания игрока с не уникальным логином")
    void savePlayerWhenLoginNotUnique() {
        repository.save(expectedLoginPlayer, expectedPasswordPlayer);
        SaveEntityException exception = Assertions.assertThrows(SaveEntityException.class, () ->
                repository.save(expectedLoginPlayer, expectedPasswordPlayer));
        String expectedErrorMessage = "Игрок с таким логином существует!";
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }
}