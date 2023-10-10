package org.example.service;

import org.example.model.Audit;
import org.example.repository.AuditInMemoryRepository;
import org.example.util.AuditType;

import java.util.List;

/**
 * Класс ответственный за бизнес-логику для сущности Audit.
 */
public class AuditService {
    private static AuditService instance;

    private final AuditInMemoryRepository auditInMemoryRepository;

    private AuditService() {
        this.auditInMemoryRepository = AuditInMemoryRepository.getInstance();
    }

    public AuditService(AuditInMemoryRepository auditInMemoryRepository) {
        this.auditInMemoryRepository = auditInMemoryRepository;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton.
     *
     * @return сущность AuditService
     */
    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    /**
     * Метод для сохранения аудита.
     *
     * @param auditType   тип аудита
     * @param loginPlayer логин игрока, который был инициатором создания аудита
     */
    public void addAudit(AuditType auditType, String loginPlayer) {
        auditInMemoryRepository.addAudit(auditType, loginPlayer);
    }

    /**
     * Метод для нахождения всех аудитов игрока по его логину.
     *
     * @param login логин игрока, который был инициатором создания аудита
     */
    public List<Audit> findAuditsByLoginPlayer(String login) {
        return auditInMemoryRepository.findAuditsByLoginPlayerByCreatedTime(login);
    }


}
