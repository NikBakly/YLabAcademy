package org.example.service;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.PlayerRequestDto;
import org.example.domain.dto.PlayerResponseDto;
import org.example.domain.dto.TransactionRequestDto;
import org.example.domain.model.Player;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.repository.PlayerRepository;
import org.example.util.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

/**
 * Класс для тестирования PlayerServiceTest
 */
class PlayerServiceTest {
    static Long playerId;
    static String loginPlayer;
    static String loginWithError;
    static String passwordPlayer;
    static String passwordWithError;
    static String blank;
    static String expectedErrorMessage;
    static long transactionId;
    static double creditSize;
    static double debitSize;
    Player expectedPlayer;
    PlayerResponseDto expectedPlayerResponseDto;
    PlayerService playerService;

    @BeforeEach
    void setPlayerService() {
        playerId = 1L;
        expectedPlayer = new Player(playerId, loginPlayer, passwordPlayer, 0.0);
        expectedPlayerResponseDto = new PlayerResponseDto(1L, loginPlayer, BigDecimal.valueOf(0.0));

        PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
        TransactionService transactionService = Mockito.mock(TransactionService.class);

        playerService = new PlayerServiceImpl(playerRepository, transactionService);

        when(playerRepository.save(loginPlayer, passwordPlayer))
                .thenReturn(expectedPlayer);

        when(playerRepository.findByLogin(loginPlayer))
                .thenReturn(Optional.ofNullable(expectedPlayer));

        when(playerRepository.findByLogin(loginWithError))
                .thenReturn(Optional.empty());
    }


    @BeforeAll
    static void init() {
        loginPlayer = "test";
        loginWithError = loginPlayer + " tester";
        passwordPlayer = "test";
        passwordWithError = passwordPlayer + 123;
        blank = " ";
        expectedErrorMessage = "Логин или пароль не может быть пустым.";
        transactionId = 1;
        creditSize = 1200;
        debitSize = creditSize - 200;
    }

    /**
     * Тест для проверки шаблона проектирования Singleton
     */
    @Test
    @DisplayName("Проверка шаблона проектирования Singleton.")
    void getInstance() {
        PlayerServiceImpl firstPointer = PlayerServiceImpl.getInstance();
        PlayerServiceImpl secondPointer = PlayerServiceImpl.getInstance();
        Assertions.assertThat(firstPointer)
                .as("Указатели ссылаются на разные объекты.")
                .isEqualTo(secondPointer);
    }

    /**
     * Тест для проверки отработки исключения при передаче в метод регистрации пустое поле login
     */
    @Test
    @DisplayName("Не удачная регистрации при пустом поле login.")
    void registrationPlayerWhenLoginIsBlank() {
        Throwable thrown = Assertions.catchThrowable(() ->
                playerService.registration(new PlayerRequestDto(blank, passwordPlayer)));
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(InvalidInputException.class)
                .hasMessage(expectedErrorMessage);
    }

    /**
     * Тест для проверки отработки исключения при передаче в метод регистрации пустое поле password
     */
    @Test
    @DisplayName("Не удачная регистрации при пустом поле password.")
    void registrationPlayerWhenPasswordIsBlank() {
        Throwable thrown = Assertions.catchThrowable(() ->
                playerService.registration(new PlayerRequestDto(loginPlayer, blank)));
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(InvalidInputException.class)
                .hasMessage(expectedErrorMessage);
    }

    /**
     * Тест для регистрации игрока
     */
    @Test
    @DisplayName("Удачная регистрации игрока.")
    void registrationPlayer() {
        PlayerResponseDto registeredPlayer = playerService.registration(new PlayerRequestDto(loginPlayer, passwordPlayer));
        Assertions.assertThat(expectedPlayerResponseDto)
                .as("Полученный игрок не совпадает с ожидаемым игроком.")
                .isEqualTo(registeredPlayer);
    }

    /**
     * Тест для авторизации игрока
     */
    @Test
    @DisplayName("Удачная авторизации игрока.")
    void authorizationPlayer() {
        playerService.registration(new PlayerRequestDto(loginPlayer, passwordPlayer));
        PlayerResponseDto authorizedPlayer = playerService.authorization(new PlayerRequestDto(loginPlayer, passwordPlayer));
        Assertions.assertThat(expectedPlayerResponseDto)
                .as("Полученный игрок не совпадает с ожидаемым игроком.")
                .isEqualTo(authorizedPlayer);
    }

    /**
     * Тест для авторизации игрока c неверным паролем
     */
    @Test
    @DisplayName("Не удачная авторизации игрока c неверным паролем.")
    void authorizationPlayerWithWrongPassword() {
        playerService.registration(new PlayerRequestDto(loginPlayer, passwordPlayer));
        Throwable thrown = Assertions.catchThrowable(() ->
                playerService.authorization(new PlayerRequestDto(loginPlayer, passwordWithError)));
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(InvalidInputException.class);
    }

    /**
     * Тест для авторизации игрока с пустым полем login
     */
    @Test
    @DisplayName("Не удачная авторизации игрока с пустым полем login.")
    void authorizationPlayerWithLoginIsBlank() {
        Throwable thrown = Assertions.catchThrowable(() ->
                playerService.authorization(new PlayerRequestDto(blank, passwordPlayer)));
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(InvalidInputException.class)
                .hasMessage(expectedErrorMessage);
    }

    /**
     * Тест для авторизации игрока с пустым полем password
     */
    @Test
    @DisplayName("Не удачная авторизации игрока с пустым полем password.")
    void authorizationPlayerWithPasswordIsBlank() {
        Throwable thrown = Assertions.catchThrowable(() ->
                playerService.authorization(new PlayerRequestDto(loginPlayer, blank)));
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(InvalidInputException.class)
                .hasMessage(expectedErrorMessage);
    }

    /**
     * Тест для проверки пополнения счета
     */
    @Test
    @DisplayName("Удачное пополнения счета игрока.")
    void creditForPlayer() {
        PlayerResponseDto registeredPlayer = playerService.creditForPlayer(loginPlayer,
                new TransactionRequestDto(1L, TransactionType.CREDIT, creditSize));
        Assertions.assertThat(BigDecimal.valueOf(creditSize))
                .as("Полученный баланс у игрока не равен ожидаемому.")
                .isEqualTo(registeredPlayer.balance());
    }


    /**
     * Тест для проверки пополнения счета, когда игрока с login не существует
     */
    @Test
    @DisplayName("Не удачное пополнения счета игрока не существующего игрока.")
    void creditForPlayerWhenLoginIsWrong() {
        Throwable thrown = Assertions.catchThrowable(() ->
                playerService.creditForPlayer(loginWithError,
                        new TransactionRequestDto(1L, TransactionType.CREDIT, creditSize)));
        String expectedMessageErrorWhenLoginIsWrong = "Игрок не найден.";
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(NotFoundException.class)
                .hasMessage(expectedMessageErrorWhenLoginIsWrong);
    }

    /**
     * Тест для проверки снятия со счета
     */
    @Test
    @DisplayName("Удачное снятия со счета игрока.")
    void debitForPlayer() {
        PlayerResponseDto registeredPlayer = playerService.creditForPlayer(loginPlayer,
                new TransactionRequestDto(1L, TransactionType.DEBIT, creditSize));
        registeredPlayer = playerService.debitForPlayer(
                loginPlayer,
                new TransactionRequestDto(transactionId + 1, TransactionType.DEBIT, debitSize));
        Assertions.assertThat(BigDecimal.valueOf(creditSize - debitSize))
                .as("Полученный баланс у игрока не равен ожидаемому.")
                .isEqualTo(registeredPlayer.balance());
    }

    /**
     * Тест для проверки снятия со счета не существующего игрока
     */
    @Test
    @DisplayName("Не удачное снятия со счета у не существующего игрока.")
    void debitForPlayerWhenLoginIsWrong() {
        Throwable thrown = Assertions.catchThrowable(() ->
                playerService.debitForPlayer(loginWithError,
                        new TransactionRequestDto(1L, TransactionType.DEBIT, 0.0)));
        String expectedNotFoundMessageError = "Игрок не найден.";
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(NotFoundException.class)
                .hasMessage(expectedNotFoundMessageError);
    }

    /**
     * Тест для проверки снятия со счета при ма
     */
    @Test
    @DisplayName("Не удачное снятия со счета игрока, когда размер дебета превышает размер баланса игрока.")
    void debitForPlayerWhenDebitSizeLargerBalancePlayer() {
        playerService.registration(new PlayerRequestDto(loginPlayer, passwordPlayer));
        String expectedMessageErrorWhenDebitSizeLargerBalancePlayer = "У вас нету столько средств на балансе.";
        Throwable thrown = Assertions.catchThrowable(() ->
                playerService.debitForPlayer(loginPlayer,
                        new TransactionRequestDto(1L, TransactionType.DEBIT, debitSize)));
        Assertions.assertThat(thrown)
                .as("Должно быть другое исключение")
                .isInstanceOf(InvalidInputException.class)
                .hasMessage(expectedMessageErrorWhenDebitSizeLargerBalancePlayer);
    }
}