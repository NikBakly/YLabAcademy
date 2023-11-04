package org.example.domain.model;

import java.math.BigDecimal;

/**
 * Сущность Player.
 */

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

    public Player(Long id, String login, String password, BigDecimal balance) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
