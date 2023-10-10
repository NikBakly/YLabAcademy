package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для хранения сущности Player в памяти компьютера
 */
public class PlayerInMemoryRepository {
    private static PlayerInMemoryRepository instance;

    /**
     * Коллекция для хранения игроков login игрока - сущность игрока
     */
    private final Map<String, Player> players;


    private PlayerInMemoryRepository() {
        players = new HashMap<>();
    }

    /**
     * Метод для реализации шаблона проектирования Singleton
     *
     * @return сущность PlayerInMemoryRepository
     */
    public static PlayerInMemoryRepository getInstance() {
        if (instance == null) {
            instance = new PlayerInMemoryRepository();
        }
        return instance;
    }

    /**
     * Метод для сохранения сущности в память
     *
     * @param login    логин игрока
     * @param password пароль игрока
     * @return созданный игрок
     * @throws SaveEntityException если переданный логин уже существует
     */
    public Player save(String login, String password) throws SaveEntityException {
        if (players.containsKey(login)) {
            throw new SaveEntityException("Игрок с таким логином существует!");
        }
        Player newPlayer = new Player(login, password);
        players.put(login, newPlayer);
        return newPlayer;
    }

    /**
     * Нахождения игрока по его логину
     *
     * @param login логин игрока
     * @return найденный игрок или null, если игрок не найден.
     */
    public Player findByLogin(String login) {
        return players.getOrDefault(login, null);
    }
}
