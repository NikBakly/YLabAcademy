package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Класс для хранения сущности Player в памяти компьютера.
 */
public class PlayerInMemoryRepository implements PlayerRepository {

    /**
     * Коллекция для хранения игроков login игрока - сущность игрока.
     */
    private final Map<String, Player> players;


    public PlayerInMemoryRepository() {
        players = new HashMap<>();
    }

    @Override
    public Player save(String loginPlayer, String password) throws SaveEntityException {
        if (players.containsKey(loginPlayer)) {
            throw new SaveEntityException("Игрок с таким логином существует!");
        }
        Player newPlayer = new Player(loginPlayer, password);
        players.put(loginPlayer, newPlayer);
        return newPlayer;
    }

    @Override
    public Optional<Player> findByLogin(String loginPlayer) {
        return Optional.ofNullable(players.getOrDefault(loginPlayer, null));
    }
}
