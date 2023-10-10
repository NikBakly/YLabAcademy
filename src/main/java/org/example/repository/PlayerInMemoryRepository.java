package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для хранения сущности Player в памяти компьютера.
 */
public class PlayerInMemoryRepository {
    private static PlayerInMemoryRepository instance;

    /**
     * Коллекция для хранения игроков login игрока - сущность игрока.
     */
    private final Map<String, Player> players;


    public PlayerInMemoryRepository() {
        players = new HashMap<>();
    }


    /**
     * Метод для сохранения сущности в память.
     *
     * @param loginPlayer логин игрока
     * @param password    пароль игрока
     * @return созданный игрок
     * @throws SaveEntityException если переданный логин уже существует
     */
    public Player save(String loginPlayer, String password) throws SaveEntityException {
        if (players.containsKey(loginPlayer)) {
            throw new SaveEntityException("Игрок с таким логином существует!");
        }
        Player newPlayer = new Player(loginPlayer, password);
        players.put(loginPlayer, newPlayer);
        return newPlayer;
    }

    /**
     * Нахождения игрока по его логину.
     *
     * @param loginPlayer логин игрока
     * @return найденный игрок или null, если игрок не найден.
     */
    public Player findByLogin(String loginPlayer) {
        return players.getOrDefault(loginPlayer, null);
    }
}
