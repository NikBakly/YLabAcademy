package org.example.mapper;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.AuditResponseDto;
import org.example.domain.model.Audit;
import org.example.util.AuditType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class AuditMapperTest {
    private final AuditMapper auditMapper = new AuditMapperImpl();


    @Test
    @DisplayName("Преобразование объекта Audit в объект AuditResponseDto")
    void testToResponseDto() {
        Audit expectedAudit = new Audit(1L, AuditType.REGISTRATION, 1L, Instant.now());
        AuditResponseDto actualAudit = auditMapper.toResponseDto(expectedAudit);

        Assertions.assertThat(actualAudit).usingRecursiveComparison()
                .withComparatorForType(Instant::compareTo, Instant.class)
                .ignoringFields("id")
                .isEqualTo(expectedAudit);
    }

    @Test
    @DisplayName("Преобразование объекта Audit в объект AuditResponseDto, когда Audit пустой")
    void testToResponseDtoWhenEntityIsBlank() {
        Audit expectedAudit = new Audit(null, null, null, null);
        AuditResponseDto actualAudit = auditMapper.toResponseDto(expectedAudit);

        Assertions.assertThat(actualAudit).usingRecursiveComparison()
                .withComparatorForType(Instant::compareTo, Instant.class)
                .ignoringFields("id")
                .isEqualTo(expectedAudit);
    }
}