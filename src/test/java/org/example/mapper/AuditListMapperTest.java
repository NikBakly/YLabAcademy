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

    private final AuditListMapper auditListMapper = new AuditListMapperImpl(new AuditMapperImpl());

    @Test
    @DisplayName("Преобразование списка объектов Audit в список объектов AuditResponseDto")
    void testToResponsesAuditDto() {
        Audit expectedFirstAudit = new Audit(1L, AuditType.REGISTRATION, 1L, Instant.now());
        Audit expectedSecondAudit = new Audit(2L, AuditType.AUTHORIZATION, 2L, Instant.now());
        List<Audit> audits = List.of(expectedFirstAudit, expectedSecondAudit);

        List<AuditResponseDto> auditsResponseDto = auditListMapper.toResponsesAuditDto(audits);

        int expectedListSize = 2;

        Assertions.assertThat(auditsResponseDto.size()).isEqualTo(expectedListSize);

        AuditResponseDto actualFirstAudit = auditsResponseDto.get(0);
        Assertions.assertThat(actualFirstAudit).usingRecursiveComparison()
                .withComparatorForType(Instant::compareTo, Instant.class)
                .ignoringFields("id")
                .isEqualTo(expectedFirstAudit);

        AuditResponseDto actualSecondAudit = auditsResponseDto.get(1);
        Assertions.assertThat(actualSecondAudit).usingRecursiveComparison()
                .withComparatorForType(Instant::compareTo, Instant.class)
                .ignoringFields("id")
                .isEqualTo(expectedSecondAudit);
    }

    @Test
    @DisplayName("Преобразование списка объектов Audit в список объектов AuditResponseDto, когда список Audit пустым")
    void testToResponsesAuditDtoWhenEntitiesIsBlank() {
        List<Audit> audits = List.of();

        List<AuditResponseDto> auditsResponseDto = auditListMapper.toResponsesAuditDto(audits);

        int expectedListSize = 0;

        Assertions.assertThat(auditsResponseDto.size()).isEqualTo(expectedListSize);

    }

}