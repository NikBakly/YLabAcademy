package org.example.model;

import java.util.Objects;

/**
 * Сущность Player.
 */
public class Player {
    private final String login;
    private final String password;
    private Double balance;


    public Player(String login, String password) {
        this.login = login;
        this.password = password;
        this.balance = 0.0;
    }


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(login, player.login) && Objects.equals(password, player.password) && Objects.equals(balance, player.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password, balance);
    }
}
