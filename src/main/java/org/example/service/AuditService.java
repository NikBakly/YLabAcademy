package org.example.service;

import org.example.model.Audit;
import org.example.util.AuditType;

import java.util.List;

/**
 * Интерфейс описывающий API бизнес-логики для сущности Audit
 */
public interface AuditService {
    /**
     * Метод для сохранения аудита.
     *
     * @param auditType   тип аудита
     * @param loginPlayer логин игрока, который был инициатором создания аудита
     */
    void addAudit(AuditType auditType, String loginPlayer);

    /**
     * Метод для нахождения всех аудитов игрока по его логину.
     *
     * @param login логин игрока, который был инициатором создания аудита
     */
    List<Audit> findAuditsByLoginPlayer(String login);

}
