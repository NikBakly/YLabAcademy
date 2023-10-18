package org.example.model;

import java.math.BigDecimal;
import java.util.Objects;

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

    public Player(Long id, String login, String password, Double balance) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.balance = BigDecimal.valueOf(balance);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(login, player.login)
                && Objects.equals(password, player.password)
                && Objects.equals(balance, player.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password, balance);
    }
}
