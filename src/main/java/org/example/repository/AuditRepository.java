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
     * @param auditType   тип
     * @param loginPlayer логин игрока
     */
    void addAudit(AuditType auditType, String loginPlayer);

    /**
     * Метод для нахождения всех аудитов игрока.
     *
     * @param loginPlayer логин игрока
     * @return список всех аудитов по логину игрока и отсортированный по времени
     */
    List<Audit> findAuditsByLoginPlayerByCreatedTime(String loginPlayer);
}
