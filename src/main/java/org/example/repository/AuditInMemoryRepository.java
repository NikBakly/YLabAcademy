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
 * Класс для хранения сущности Audit в памяти компьютера.
 */
public class AuditInMemoryRepository {
    private static AuditInMemoryRepository instance;

    /**
     * Коллекция для хранения аудита по их id.
     */
    private final Map<Long, Audit> audits;
    private long nextId;

    private AuditInMemoryRepository() {
        audits = new HashMap<>();
        nextId = 1L;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton.
     *
     * @return сущность AuditService
     */
    public static AuditInMemoryRepository getInstance() {
        if (instance == null) {
            instance = new AuditInMemoryRepository();
        }
        return instance;
    }

    /**
     * Метод для добавления аудита.
     *
     * @param auditType   тип
     * @param loginPlayer логин игрока
     */
    public void addAudit(AuditType auditType, String loginPlayer) {
        Audit newAudit = new Audit(nextId, auditType, loginPlayer, Instant.now());
        audits.put(nextId, newAudit);
        ++nextId;
    }

    /**
     * Метод для нахождения всех аудитов игрока.
     *
     * @param loginPlayer логин игрока
     * @return список всех аудитов по логину игрока и отсортированный по времени
     */
    public List<Audit> findAuditsByLoginPlayerByCreatedTime(String loginPlayer) {
        return audits.values().stream()
                .filter(audit -> audit.loginPlayer().equals(loginPlayer))
                .sorted(Comparator.comparing(Audit::createdTime))
                .collect(Collectors.toList());
    }
}
