package org.example.domain.dto;

import org.example.util.TransactionType;

/**
 * Класс обрабатывающий запрос для сущности Player
 *
 * @param id   идентификатор транзакции
 * @param type тип транзакции
 * @param size размер транзакции
 */
public record TransactionRequestDto(Long id,
                                    TransactionType type,
                                    Double size) {
}
