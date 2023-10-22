package org.example.model;

import org.example.util.TransactionType;

import java.time.Instant;

/**
 * Сущность Transaction.
 *
 * @param id          идентификатор сущности
 * @param type        тип транзакции
 * @param size        размер транзакции
 * @param playerId    идентификатор игрока, который был инициатором создания транзакции
 * @param createdTime
 */
public record Transaction(Long id,
                          TransactionType type,
                          Double size,
                          Long playerId,
                          Instant createdTime) {
}
