package org.example.service;

import org.example.dto.AuditResponseDto;
import org.example.util.AuditType;

import java.util.List;

/**
 * Интерфейс описывающий API бизнес-логики для сущности Audit
 */
public interface AuditService {
    /**
     * Метод для сохранения аудита.
     *
     * @param auditType тип аудита
     * @param playerId  идентификатор игрока, который был инициатором создания аудита
     */
    void createAudit(AuditType auditType, Long playerId);

    /**
     * Метод для сохранения аудита.
     *
     * @param auditType   тип аудита
     * @param loginPlayer логин игрока, который был инициатором создания аудита
     */
    void createAudit(AuditType auditType, String loginPlayer);

    /**
     * Метод для нахождения всех аудитов игрока по его логину.
     *
     * @param playerId идентификатор игрока, который был инициатором создания аудита
     */
    List<AuditResponseDto> findAuditsByLoginPlayer(Long playerId);

}