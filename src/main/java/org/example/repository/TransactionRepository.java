package org.example.repository;

import org.example.dto.TransactionResponseDto;
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
     * @param newTransaction новая транзакция
     * @throws SaveEntityException ошибка при попытке создания транзакции
     */
    void createdTransaction(Transaction newTransaction) throws SaveEntityException;

    /**
     * Метод для нахождения всех транзакций определенного типа по логину игрока и отсортированный по времени.
     *
     * @param playerId        идентификатор игрока
     * @param transactionType тип транзакции
     * @return список всех транзакций определенного типа по логину игрока и отсортированный по времени
     */
    List<TransactionResponseDto> findHistoryTransactionsByCreatedTime(Long playerId, TransactionType transactionType);

}
