package org.example.mapper;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.TransactionRequestDto;
import org.example.domain.model.Transaction;
import org.example.util.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class TransactionMapperTest {

    @Test
    @DisplayName("Преобразование объекта TransactionRequestDto в объект Transaction")
    void testToEntity() {
        Long expectedPlayerId = 1L;
        TransactionRequestDto expectedTransaction =
                new TransactionRequestDto(1L, TransactionType.CREDIT, 0.0);

        Transaction actualTransaction = TransactionMapper.INSTANCE.toEntity(expectedTransaction, expectedPlayerId);

        Assertions.assertThat(actualTransaction.playerId().equals(expectedPlayerId) &&
                        actualTransaction.id().equals(expectedTransaction.id()) &&
                        actualTransaction.type().equals(expectedTransaction.type()) &&
                        actualTransaction.size().equals(expectedTransaction.size()))
                .isTrue();
    }

}