package org.example.repository;

import org.example.model.Audit;
import org.example.util.AuditType;

import java.util.List;

/**
 * Интерфейс описывающий API для действий с сущностью Audit
 */
public interface AuditRepository {
    /**
     * Метод для добавления аудита.
     *
     * @param auditType тип
     * @param playerId  идентификатор игрока
     */
    void createAudit(AuditType auditType, Long playerId);

    /**
     * Метод для нахождения всех аудитов игрока.
     *
     * @param playerId идентификатор игрока
     * @return список всех аудитов по логину игрока и отсортированный по времени
     */
    List<Audit> findAuditsByPlayerIdByCreatedTime(Long playerId);
}