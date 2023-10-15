package org.example.repository;

import org.example.model.Audit;
import org.example.util.AuditType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для хранения сущности Audit в памяти компьютера.
 */
public class AuditInMemoryRepository implements AuditRepository {

    /**
     * Коллекция для хранения аудита по loginPlayer.
     */
    private final Map<String, List<Audit>> audits;

    public AuditInMemoryRepository() {
        audits = new HashMap<>();
    }


    @Override
    public void addAudit(AuditType auditType, String loginPlayer) {
        Audit newAudit = new Audit(auditType, loginPlayer, Instant.now());
        List<Audit> foundAuditsByLoginPlayer = audits.getOrDefault(loginPlayer, null);
        if (foundAuditsByLoginPlayer == null) {
            foundAuditsByLoginPlayer = new ArrayList<>();
        }
        foundAuditsByLoginPlayer.add(newAudit);
        audits.put(loginPlayer, foundAuditsByLoginPlayer);
    }

    @Override
    public List<Audit> findAuditsByLoginPlayerByCreatedTime(String loginPlayer) {
        return audits.get(loginPlayer);
    }
}
