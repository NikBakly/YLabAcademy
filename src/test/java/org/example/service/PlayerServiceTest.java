package org.example.service;

import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.model.Player;
import org.example.repository.PlayerInMemoryRepository;
import org.example.repository.TransactionInMemoryRepository;
import org.junit.jupiter.api.*;

/**
 * Класс для тестирования PlayerServiceTest
 */
class PlayerServiceTest {
    static String loginPlayer;
    static String passwordPlayer;
    static String blank;
    static Player expectedPlayer;
    static String expectedErrorMessage;
    static long transactionId;
    PlayerService playerService;

    @BeforeAll
    static void init() {
        loginPlayer = "test";
        passwordPlayer = "test";
        blank = " ";
        expectedPlayer = new Player(loginPlayer, passwordPlayer);
        expectedErrorMessage = "Логин или пароль не может быть пустым.";
        transactionId = 1;
    }

    @BeforeEach
    void setPlayerService() {
        playerService = new PlayerService(
                new PlayerInMemoryRepository(),
                new TransactionService(new TransactionInMemoryRepository()));
    }

    /**
     * Тест для проверки шаблона проектирования Singleton
     */
    @Test
    @DisplayName("Тест 1. Проверка шаблона проектирования Singleton.")
    void getInstance() {
        PlayerService firstPointer = PlayerService.getInstance();
        PlayerService secondPointer = PlayerService.getInstance();
        Assertions.assertSame(firstPointer, secondPointer, "Указатели ссылаются на разные объекты.");
    }

    /**
     * Тест для проверки отработки исключения при передаче в метод регистрации пустое поле login
     */
    @Test
    @DisplayName("Тест 2. Не удачная регистрации при пустом поле login.")
    void registrationPlayerWhenLoginIsBlank() {
        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class, () ->
                        playerService.registration(blank, passwordPlayer),
                "Ожидаемое исключение не было отловлено");
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage(),
                "Ожидаемое сообщение об ошибки не совпадает с полученным результатом");
    }

    /**
     * Тест для проверки отработки исключения при передаче в метод регистрации пустое поле password
     */
    @Test
    @DisplayName("Тест 3. Не удачная регистрации при пустом поле password.")
    void registrationPlayerWhenPasswordIsBlank() {
        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class, () ->
                        playerService.registration(loginPlayer, blank),
                "Ожидаемое исключение не было отловлено");
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage(),
                "Ожидаемое сообщение об ошибки не совпадает с полученным результатом");
    }

    /**
     * Тест для регистрации игрока
     */
    @Test
    @DisplayName("Тест 4. Удачная регистрации игрока.")
    void registrationPlayer() {
        Player registeredPlayer = playerService.registration(loginPlayer, passwordPlayer);
        Assertions.assertEquals(expectedPlayer, registeredPlayer,
                "Полученный игрок не совпадает с ожидаемым игроком");
    }

    /**
     * Тест для авторизации игрока
     */
    @Test
    @DisplayName("Тест 5. Удачная авторизации игрока.")
    void authorizationPlayer() {
        playerService.registration(loginPlayer, passwordPlayer);
        Player authorizedPlayer = playerService.authorization(loginPlayer, passwordPlayer);
        Assertions.assertEquals(expectedPlayer, authorizedPlayer,
                "Полученный игрок не совпадает с ожидаемым игроком");
    }

    /**
     * Тест для авторизации игрока c неверным паролем
     */
    @Test
    @DisplayName("Тест 6. Не удачная авторизации игрока c неверным паролем.")
    void authorizationPlayerWithWrongPassword() {
        playerService.registration(loginPlayer, passwordPlayer);
        Player authorizedPlayer = playerService.authorization(loginPlayer, passwordPlayer + "123");
        Assertions.assertNull(authorizedPlayer, "Полученный игрок должен быть пустым");
    }

    /**
     * Тест для авторизации игрока с пустым полем login
     */
    @Test
    @DisplayName("Тест 7. Не удачная авторизации игрока с пустым полем login.")
    void authorizationPlayerWithLoginIsBlank() {
        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class, () ->
                        playerService.registration(blank, passwordPlayer),
                "Ожидаемое исключение не было отловлено");
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage(),
                "Ожидаемое сообщение об ошибки не совпадает с полученным результатом");
    }

    /**
     * Тест для авторизации игрока с пустым полем password
     */
    @Test
    @DisplayName("Тест 8. Не удачная авторизации игрока с пустым полем password.")
    void authorizationPlayerWithPasswordIsBlank() {
        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class, () ->
                        playerService.registration(loginPlayer, blank),
                "Ожидаемое исключение не было отловлено");
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage(),
                "Ожидаемое сообщение об ошибки не совпадает с полученным результатом");
    }

    /**
     * Тест для проверки пополнения счета
     */
    @Test
    @DisplayName("Тест 9. Удачное пополнения счета игрока.")
    void creditForPlayer() {
        Player registeredPlayer = playerService.registration(loginPlayer, passwordPlayer);
        double creditSize = 1200;
        playerService.creditForPlayer(loginPlayer, transactionId, creditSize);
        Assertions.assertEquals(creditSize, registeredPlayer.getBalance(),
                "Полученный баланс у игрока не равен ожидаемому");
    }

    /**
     * Тест для проверки пополнения счета при переполнении
     */
    @Test
    @DisplayName("Тест 10. Не удачное пополнения счета игрока из-за переполнения типа double.")
    void creditForPlayerWhenOverflowing() {
        playerService.registration(loginPlayer, passwordPlayer);
        double creditSize = Double.MAX_VALUE;
        playerService.creditForPlayer(loginPlayer, transactionId, creditSize);

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class, () ->
                        playerService.creditForPlayer(loginPlayer, transactionId + 1, creditSize),
                "Ожидаемое исключение не было отловлено");

        String expectedMessageErrorWhenOverflowing = "Уменьшите размер кредита";
        Assertions.assertEquals(expectedMessageErrorWhenOverflowing, exception.getMessage(),
                "Ожидаемое сообщение об ошибки не совпадает с полученным результатом");
    }

    /**
     * Тест для проверки пополнения счета, когда игрока с login не существует
     */
    @Test
    @DisplayName("Тест 11. Не удачное пополнения счета игрока не существующего игрока.")
    void creditForPlayerWhenLoginIsWrong() {
        double creditSize = 1200;
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () ->
                        playerService.creditForPlayer(loginPlayer, transactionId, creditSize),
                "Ожидаемое исключение не было отловлено");

        String expectedMessageErrorWhenOverflowing = "Игрок не найден.";
        Assertions.assertEquals(expectedMessageErrorWhenOverflowing, exception.getMessage());
    }

    /**
     * Тест для проверки снятия со счета
     */
    @Test
    @DisplayName("Тест 12. Удачное снятия со счета игрока.")
    void debitForPlayer() {
        Player registeredPlayer = playerService.registration(loginPlayer, passwordPlayer);
        double creditSize = 1200;
        double debitSize = creditSize - 200;
        playerService.creditForPlayer(loginPlayer, transactionId, creditSize);
        playerService.debitForPlayer(loginPlayer, transactionId + 1, debitSize);
        Assertions.assertEquals(creditSize - debitSize, registeredPlayer.getBalance(),
                "Полученный баланс у игрока не равен ожидаемому");
    }

    /**
     * Тест для проверки снятия со счета не существующего игрока
     */
    @Test
    @DisplayName("Тест 13. Не удачное снятия со счета не существующего игрока.")
    void debitForPlayerWhenLoginIsWrong() {
        double debitSize = 1200;
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () ->
                        playerService.debitForPlayer(loginPlayer, transactionId, debitSize),
                "Ожидаемое исключение не было отловлено");

        String expectedMessageErrorWhenOverflowing = "Игрок не найден.";
        Assertions.assertEquals(expectedMessageErrorWhenOverflowing, exception.getMessage());
    }

    /**
     * Тест для проверки снятия со счета при ма
     */
    @Test
    @DisplayName("Тест 14. Не удачное снятия со счета игрока, когда размер дебета превышает размер баланса игрока.")
    void debitForPlayerWhenDebitSizeLargerBalancePlayer() {
        playerService.registration(loginPlayer, passwordPlayer);
        double debitSize = 200;
        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class, () ->
                        playerService.debitForPlayer(loginPlayer, transactionId, debitSize),
                "Ожидаемое исключение не было отловлено");

        String expectedMessageErrorWhenOverflowing = "У вас нету столько средств на балансе.";
        Assertions.assertEquals(expectedMessageErrorWhenOverflowing, exception.getMessage());
    }
}