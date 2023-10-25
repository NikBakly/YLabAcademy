package org.example.dto;

/**
 * Класс обрабатывающий запрос для сущности Player
 *
 * @param login    логин игрока
 * @param password пароль игрока
 */
public record PlayerRequestDto(String login,
                               String password) {
}
