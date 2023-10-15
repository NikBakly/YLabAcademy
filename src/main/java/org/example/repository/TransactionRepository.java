package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.util.TransactionType;

import java.util.List;

/**
 * Интерфейс описывающий API для действий с сущностью Transaction
 */
public interface TransactionRepository {
    /**
     * Метод для создания транзакции.
     *
     * @param transactionId   уникальное id транзакции
     * @param transactionType тип транзакции
     * @param size            размер транзакции
     * @param loginPlayer     логин игрока
     * @throws SaveEntityException ошибка при попытке создания транзакции
     */
    void createdTransaction(Long transactionId,
                            TransactionType transactionType,
                            Double size,
                            String loginPlayer) throws SaveEntityException;

    /**
     * Метод для нахождения всех транзакций типа CREDIT по логину игрока и отсортированный по времени.
     *
     * @param loginPlayer логин игрока
     * @return список всех транзакций типа CREDIT по логину игрока и отсортированный по времени
     */
    List<Transaction> findCreditHistoryTransactionsByCreatedTime(String loginPlayer);

    /**
     * Метод для нахождения всех транзакций типа DEBIT по логину игрока и отсортированный по времени.
     *
     * @param loginPlayer логин игрока
     * @return список всех транзакций типа DEBIT по логину игрока и отсортированный по времени
     */
    List<Transaction> findDebitHistoryTransactionsByCreatedTime(String loginPlayer);
}
