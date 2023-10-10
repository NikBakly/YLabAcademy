package org.example.model;

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
}
