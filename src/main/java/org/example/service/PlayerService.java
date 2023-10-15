package org.example.service;

import org.example.exception.InvalidInputException;
import org.example.exception.SaveEntityException;
import org.example.model.Player;

/**
 * Интерфейс описывающий API бизнес-логики для сущности Player
 */
public interface PlayerService {
    /**
     * Метод для регистрации игрока.
     *
     * @param login    логин игрока.
     * @param password пароль игрока.
     * @return зарегистрированный игрок.
     * @throws InvalidInputException если переданные данные пустые.
     * @throws SaveEntityException   ошибка при создании сущности.
     */
    Player registration(String login, String password) throws InvalidInputException, SaveEntityException;

    /**
     * Метод для авторизации игрока.
     *
     * @param login    логин игрока.
     * @param password пароль игрока.
     * @return зарегистрированный игрок.
     * @throws InvalidInputException если переданные данные пустые.
     */
    Player authorization(String login, String password) throws InvalidInputException;

    /**
     * Метод для выполнения дебет(списание средств) операции по логину игрока.
     *
     * @param loginPlayer   логин игрока, к которому будет выполнена операция дебет.
     * @param transactionId уникальный id транзакции.
     * @param debitSize     размер средств для списания.
     * @throws RuntimeException ошибка при не выполненной операции.
     */
    void debitForPlayer(String loginPlayer, long transactionId, double debitSize) throws RuntimeException;

    /**
     * Метод для выполнения кредит(пополнения средств) операции по логину игрока.
     *
     * @param loginPlayer   логин игрока, к которому будет выполнена операция дебет.
     * @param transactionId уникальный id транзакции.
     * @param creditSize    размер средств для пополнения.
     * @throws RuntimeException ошибка при не выполненной операции.
     */
    void creditForPlayer(String loginPlayer, long transactionId, double creditSize) throws RuntimeException;

}
