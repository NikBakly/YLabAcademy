package org.example.dto;

import org.example.util.TransactionType;

/**
 * Класс обрабатывающий запрос для сущности Player
 *
 * @param id       идентификатор транзакции
 * @param type     тип транзакции
 * @param playerId идентификатор игрока, который был инициатором создания транзакции
 * @param size     размер транзакции
 */
public record TransactionRequestDto(Long id,
                                    TransactionType type,
                                    Long playerId,
                                    Double size) {
}
