package org.example.service;

import org.assertj.core.api.Assertions;
import org.example.model.Audit;
import org.example.util.AuditType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Класс для тестирования AuditService
 */
class AuditServiceTest {
    static AuditService service;
    static String loginPlayer;

    @BeforeAll
    static void init() {
        service = Mockito.mock(AuditService.class);
        loginPlayer = "tester";
        when(service.findAuditsByLoginPlayer(loginPlayer))
                .thenReturn(List.of(
                        new Audit(1L, AuditType.REGISTRATION, loginPlayer, Instant.now()))
                );
    }

    /**
     * Тест для проверки шаблона проектирования Singleton
     */
    @Test
    @DisplayName("Проверка шаблона проектирования Singleton.")
    void getInstance() {
        AuditServiceImpl first = AuditServiceImpl.getInstance();
        AuditServiceImpl secondPointer = AuditServiceImpl.getInstance();
        Assertions.assertThat(first)
                .as("Указатели ссылаются на разные объекты.")
                .isEqualTo(secondPointer);
    }

    /**
     * Тест сохранения аудита и его нахождения по логину пользователя
     */
    @Test
    @DisplayName("Удачное создание и нахождение аудита по логину игрока")
    void createAndFindAuditByLoginPlayer() {
        AuditType auditType = AuditType.REGISTRATION;
        service.addAudit(auditType, loginPlayer);
        Audit foundAudit = service.findAuditsByLoginPlayer(loginPlayer).get(0);
        Assertions.assertThat(foundAudit.loginPlayer().equals(loginPlayer) &&
                        foundAudit.type().equals(auditType))
                .as("Аудит не соответствует ожиданиям")
                .isTrue();
    }

}