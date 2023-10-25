package org.example.service;

import org.example.dto.PlayerRequestDto;
import org.example.dto.PlayerResponseDto;
import org.example.dto.TransactionRequestDto;
import org.example.exception.InvalidInputException;
import org.example.exception.SaveEntityException;

/**
 * Интерфейс описывающий API бизнес-логики для сущности Player
 */
public interface PlayerService {
    /**
     * Метод для регистрации игрока.
     *
     * @param playerRequestDto данные об игрока полученные из запроса
     * @return зарегистрированный игрок.
     * @throws InvalidInputException если переданные данные пустые.
     * @throws SaveEntityException   ошибка при создании сущности.
     */
    PlayerResponseDto registration(PlayerRequestDto playerRequestDto) throws InvalidInputException, SaveEntityException;

    /**
     * Метод для авторизации игрока.
     *
     * @param playerRequestDto данные об игрока полученные из запроса
     * @return зарегистрированный игрок.
     * @throws InvalidInputException если переданные данные пустые.
     */
    PlayerResponseDto authorization(PlayerRequestDto playerRequestDto) throws InvalidInputException;

    /**
     * Метод для выполнения дебет(списание средств) операции по логину игрока.
     *
     * @param loginPlayer   логин игрока, к которому будет выполнена операция дебет.
     * @param transactionId уникальный id транзакции.
     * @param debitSize     размер средств для списания.
     * @throws RuntimeException ошибка при не выполненной операции.
     */
    PlayerResponseDto debitForPlayer(String loginPlayer, TransactionRequestDto transactionRequestDto)
            throws RuntimeException;

    /**
     * Метод для выполнения кредит(пополнения средств) операции по логину игрока.
     *
     * @param loginPlayer   логин игрока, к которому будет выполнена операция дебет.
     * @param transactionId уникальный id транзакции.
     * @param creditSize    размер средств для пополнения.
     * @throws RuntimeException ошибка при не выполненной операции.
     */
    PlayerResponseDto creditForPlayer(String loginPlayer, TransactionRequestDto transactionRequestDto)
            throws RuntimeException;

}
