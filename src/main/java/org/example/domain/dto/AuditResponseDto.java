package org.example.domain.dto;

import org.example.util.AuditType;

import java.time.Instant;

/**
 * Класс для передачи аудита игрока
 *
 * @param type        тип аудита
 * @param playerId    идентификатор игрока, который был инициатором создания аудита
 * @param createdTime дата создания
 */
public record AuditResponseDto(AuditType type,
                               Long playerId,
                               Instant createdTime) {
}
