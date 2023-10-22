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
     * @param newTransaction новая транзакция
     * @throws SaveEntityException ошибка при попытке создания транзакции
     */
    void createTransaction(Transaction newTransaction) throws SaveEntityException;

    /**
     * Метод запрашивает все транзакций определенного типа по логину игрока и отсортированный по времени создания.
     *
     * @param playerId логин игрока
     * @return список всех транзакций определенного типа по логину игрока и отсортированный по времени
     */
    List<Transaction> getHistoryTransactions(Long playerId, TransactionType transactionType);

}
