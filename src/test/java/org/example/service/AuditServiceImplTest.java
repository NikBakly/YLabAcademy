package org.example.service;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.AuditResponseDto;
import org.example.domain.model.Audit;
import org.example.mapper.AuditListMapper;
import org.example.mapper.AuditListMapperImpl;
import org.example.mapper.AuditMapperImpl;
import org.example.repository.AuditRepository;
import org.example.repository.PlayerRepository;
import org.example.util.AuditType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Класс для тестирования AuditService
 */
@SpringBootTest
class AuditServiceImplTest {
    static AuditService service;
    static Long playerId;

    @BeforeAll
    static void init() {
        AuditRepository auditRepository = Mockito.mock(AuditRepository.class);
        PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
        AuditListMapper auditListMapper = new AuditListMapperImpl(new AuditMapperImpl());
        service = new AuditServiceImpl(auditRepository, playerRepository, auditListMapper);
        playerId = 1L;
        when(auditRepository.findAuditsByPlayerIdByCreatedTime(playerId))
                .thenReturn(List.of(
                        new Audit(1L, AuditType.REGISTRATION, playerId, Instant.now()))
                );
    }

    /**
     * Тест сохранения аудита и его нахождения по логину пользователя
     */
    @Test
    @DisplayName("Удачное создание и нахождение аудита по идентификатору игрока")
    void testCreateAndFindAuditByLoginPlayer() {
        AuditType auditType = AuditType.REGISTRATION;
        service.createAudit(auditType, playerId);
        AuditResponseDto foundAudit = service.findAuditsByLoginPlayer(playerId).get(0);
        Assertions.assertThat(foundAudit.playerId().equals(playerId) &&
                        foundAudit.type().equals(auditType))
                .as("Аудит не соответствует ожиданиям")
                .isTrue();
    }

}