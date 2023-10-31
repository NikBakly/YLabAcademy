package org.example.mapper;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.AuditResponseDto;
import org.example.domain.model.Audit;
import org.example.util.AuditType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class AuditMapperTest {

    @Test
    @DisplayName("Преобразование объекта Audit в объект AuditResponseDto")
    void testToResponseDto() {
        Audit expectedAudit = new Audit(1L, AuditType.REGISTRATION, 1L, Instant.now());

        AuditResponseDto actualAudit = AuditMapper.INSTANCE.toResponseDto(expectedAudit);

        Assertions.assertThat(expectedAudit.playerId().equals(actualAudit.playerId()) &&
                        expectedAudit.type().equals(actualAudit.type()) &&
                        expectedAudit.createdTime().equals(actualAudit.createdTime()))
                .isTrue();
    }
}