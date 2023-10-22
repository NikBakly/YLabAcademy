package org.example.service;

import org.example.model.Audit;
import org.example.repository.AuditRepository;
import org.example.repository.AuditRepositoryImpl;
import org.example.util.AuditType;

import java.util.List;

/**
 * Класс реализующий бизнес-логику для сущности Audit.
 */
public class AuditServiceImpl implements AuditService {
    private static AuditServiceImpl instance;

    private final AuditRepository auditRepository;

    private AuditServiceImpl() {
        this.auditRepository = new AuditRepositoryImpl();
    }

    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton.
     *
     * @return сущность AuditService
     */
    public static AuditServiceImpl getInstance() {
        if (instance == null) {
            instance = new AuditServiceImpl();
        }
        return instance;
    }


    @Override
    public void addAudit(AuditType auditType, Long playerId) {
        auditRepository.addAudit(auditType, playerId);
    }

    public
    @Override
    List<Audit> findAuditsByLoginPlayer(Long playerId) {
        return auditRepository.findAuditsByPlayerIdByCreatedTime(playerId);
    }
}
