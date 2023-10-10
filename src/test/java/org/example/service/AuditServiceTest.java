package org.example.service;

import org.example.model.Audit;
import org.example.repository.AuditInMemoryRepository;
import org.example.util.AuditType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Класс для тестирования AuditService
 */
class AuditServiceTest {
    static AuditService service;
    static String loginPlayer;

    @BeforeAll
    static void init() {
        service = new AuditService(new AuditInMemoryRepository());
        loginPlayer = "tester";
    }

    /**
     * Тест для проверки шаблона проектирования Singleton
     */
    @Test
    @DisplayName("Тест 1. Проверка шаблона проектирования Singleton.")
    void getInstance() {
        AuditService first = AuditService.getInstance();
        AuditService secondPointer = AuditService.getInstance();
        Assertions.assertSame(first, secondPointer, "Указатели ссылаются на разные объекты.");
    }

    /**
     * Тест сохранения аудита и его нахождения по логину пользователя
     */
    @Test
    @DisplayName("Тест 2. Удачное создание и нахождение аудита по логину игрока")
    void createAndFindAuditByLoginPlayer() {
        AuditType auditType = AuditType.REGISTRATION;
        service.addAudit(auditType, loginPlayer);
        Audit foundAudit = service.findAuditsByLoginPlayer(loginPlayer).get(0);
        Assertions.assertTrue(
                foundAudit.loginPlayer().equals(loginPlayer) &&
                        foundAudit.type().equals(auditType),
                "Аудит не соответствует ожиданиям");
    }

}