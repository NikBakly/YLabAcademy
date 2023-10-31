package org.example.mapper;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.AuditResponseDto;
import org.example.domain.model.Audit;
import org.example.util.AuditType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

class AuditListMapperTest {

    @Test
    @DisplayName("Преобразование списка объектов Audit в список объектов AuditResponseDto")
    void testToResponsesAuditDto() {
        Audit expectedFirstAudit = new Audit(1L, AuditType.REGISTRATION, 1L, Instant.now());
        Audit expectedSecondAudit = new Audit(2L, AuditType.AUTHORIZATION, 2L, Instant.now());
        List<Audit> audits = List.of(expectedFirstAudit, expectedSecondAudit);

        List<AuditResponseDto> auditsResponseDto = AuditListMapper.INSTANCE.toResponsesAuditDto(audits);

        int expectedListSize = 2;

        Assertions.assertThat(auditsResponseDto.size()).isEqualTo(expectedListSize);
        AuditResponseDto actualFirstAudit = auditsResponseDto.get(0);
        Assertions.assertThat(actualFirstAudit.playerId().equals(expectedFirstAudit.playerId()) &&
                        actualFirstAudit.type().equals(expectedFirstAudit.type()) &&
                        actualFirstAudit.createdTime().equals(expectedFirstAudit.createdTime()))
                .isTrue();
        AuditResponseDto actualSecondAudit = auditsResponseDto.get(1);
        Assertions.assertThat(actualSecondAudit.playerId().equals(expectedSecondAudit.playerId()) &&
                        actualSecondAudit.type().equals(expectedSecondAudit.type()) &&
                        actualSecondAudit.createdTime().equals(expectedSecondAudit.createdTime()))
                .isTrue();
    }

}