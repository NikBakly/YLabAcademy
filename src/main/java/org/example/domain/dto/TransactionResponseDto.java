package org.example.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.util.TransactionType;

import java.time.Instant;

/**
 * Класс для передачи данных о транзакции
 *
 * @param id          идентификатор транзакции
 * @param type        тип транзакции
 * @param size        размер транзакции
 * @param loginPlayer транзакция игрока по его логину
 * @param createdTime дата создания транзакции
 */
public record TransactionResponseDto(Long id,
                                     TransactionType type,
                                     Double size,
                                     String loginPlayer,
                                     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
                                     Instant createdTime) {
}
