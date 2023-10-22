package org.example.model;

import org.example.util.AuditType;

import java.time.Instant;

/**
 * Сущность Audit.
 *
 * @param type        тип аудита
 * @param playerId    идентификатор игрока, который был инициатором создания аудита
 * @param createdTime дата создания
 */
public record Audit(Long id,
                    AuditType type,
                    Long playerId,
                    Instant createdTime) {
}
