package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Класс для тестирования PlayerInMemoryRepository
 */
class PlayerInMemoryRepositoryTest {
    static PlayerInMemoryRepository repository;

    @BeforeAll
    static void init() {
        repository = PlayerInMemoryRepository.getInstance();
    }

    /**
     * Тестирование шаблона проектирования Singleton
     */
    @Test
    @DisplayName("Тест 1. Проверка шаблона проектирования Singleton.")
    void getInstance() {
        PlayerInMemoryRepository secondPointer = PlayerInMemoryRepository.getInstance();
        Assertions.assertSame(repository, secondPointer, "Указатели ссылаются на разные объекты.");
    }

    /**
     * Тестирование создания и нахождения игрока по его логину
     */
    @Test
    @DisplayName("Тест 2. Проверка создания и нахождения игрока по его логину")
    void saveAndFindPlayerByLogin() {
        String expectedLoginPlayer = "test";
        String expectedPasswordPlayer = "test";
        repository.save(expectedLoginPlayer, expectedPasswordPlayer);
        Player foundPlayer = repository.findByLogin(expectedLoginPlayer);
        Assertions.assertEquals(expectedLoginPlayer, foundPlayer.getLogin(), "Логины не равны");
        Assertions.assertEquals(expectedPasswordPlayer, foundPlayer.getPassword(), "Логины не равны");
    }

    /**
     * Тестирование создания игрока с не уникальным логином
     */
    @Test
    @DisplayName("Тест 3. Попытка создания игрока с не уникальным логином")
    void savePlayerWhenLoginNotUnique() {
        String expectedLoginPlayer = "loginTest";
        String expectedPasswordPlayer = "passwordTest";
        repository.save(expectedLoginPlayer, expectedPasswordPlayer);
        SaveEntityException exception = Assertions.assertThrows(SaveEntityException.class, () ->
                repository.save(expectedLoginPlayer, expectedPasswordPlayer));
        String expectedErrorMessage = "Игрок с таким логином существует!";
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }
}