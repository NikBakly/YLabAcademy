package org.example.service;

import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.util.TransactionType;

import java.util.List;

/**
 * Интерфейс описывающий API бизнес-логики для сущности Transaction
 */
public interface TransactionService {
    /**
     * Метод для создания транзакции.
     *
     * @param transactionId   уникальное id транзакции
     * @param transactionType тип транзакции
     * @param size            размер транзакции
     * @param loginPlayer     логин игрока
     * @throws SaveEntityException ошибка при попытке создания транзакции
     */
    void createTransaction(Long transactionId,
                           TransactionType transactionType,
                           Double size,
                           String loginPlayer) throws SaveEntityException;

    /**
     * Метод запрашивает все транзакций определенного типа по логину игрока и отсортированный по времени создания.
     *
     * @param loginPlayer логин игрока
     * @return список всех транзакций определенного типа по логину игрока и отсортированный по времени
     */
    List<Transaction> getHistoryTransactions(String loginPlayer, TransactionType transactionType);

}
