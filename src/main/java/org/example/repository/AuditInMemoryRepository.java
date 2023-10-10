package org.example.repository;

import org.example.model.Audit;
import org.example.util.AuditType;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс отвечающий за хранение сущности Audit
 */
public class AuditInMemoryRepository {
    private static AuditInMemoryRepository instance;

    private final Map<Long, Audit> audits;
    private long nextId;

    private AuditInMemoryRepository() {
        audits = new HashMap<>();
        nextId = 1L;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton
     *
     * @return сущность AuditService
     */
    public static AuditInMemoryRepository getInstance() {
        if (instance == null) {
            instance = new AuditInMemoryRepository();
        }
        return instance;
    }

    public void addAudit(AuditType auditType, String loginPlayer) {
        Audit newAudit = new Audit(nextId, auditType, loginPlayer, Instant.now());
        audits.put(nextId, newAudit);
        ++nextId;
    }

    public List<Audit> findAuditsByLoginPlayerByCreatedTime(String login) {
        return audits.values().stream()
                .filter(audit -> audit.loginPlayer().equals(login))
                .sorted(Comparator.comparing(Audit::createdTime))
                .collect(Collectors.toList());
    }
}
