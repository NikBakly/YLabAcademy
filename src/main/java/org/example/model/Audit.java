package org.example.model;

import org.example.util.AuditType;

import java.time.Instant;

/**
 * Сущность Audit.
 *
 * @param id          идентификатор сущности
 * @param type        тип аудита
 * @param loginPlayer логин игрока, который был инициатором создания аудита
 * @param createdTime дата создания
 */
public record Audit(Long id, AuditType type, String loginPlayer, Instant createdTime) {
}
