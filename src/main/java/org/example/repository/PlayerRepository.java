package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Player;

import java.util.Optional;

/**
 * Интерфейс описывающий API для действий с сущностью Player
 */
public interface PlayerRepository {

    /**
     * Метод для сохранения сущности.
     *
     * @param loginPlayer логин игрока
     * @param password    пароль игрока
     * @return созданный игрок
     * @throws SaveEntityException если переданный логин уже существует
     */
    Player save(String loginPlayer, String password) throws SaveEntityException;

    /**
     * Нахождения игрока по его логину.
     *
     * @param loginPlayer логин игрока
     * @return найденный игрок или null, если игрок не найден.
     */
    Optional<Player> findByLogin(String loginPlayer);

    /**
     * Метод для обновления игрока в таблице БД
     *
     * @param loginPlayer логин игрока
     */
    void updateBalanceByLogin(String loginPlayer, double newBalance);

}
