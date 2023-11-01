package org.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Сущность Player.
 */
@AllArgsConstructor
@Getter
@Setter
public class Player {
    /**
     * Идентификатор игрока
     */
    private final Long id;
    /**
     * Логин игрока.
     */
    private final String login;
    /**
     * Пароль игрока.
     */
    private final String password;
    /**
     * Количество средств игрока.
     */
    private BigDecimal balance;
}
