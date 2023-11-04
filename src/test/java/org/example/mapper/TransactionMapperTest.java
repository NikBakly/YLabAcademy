package org.example.mapper;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.TransactionRequestDto;
import org.example.domain.model.Transaction;
import org.example.util.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class TransactionMapperTest {
    private final TransactionMapper transactionMapper = new TransactionMapperImpl();

    @Test
    @DisplayName("Преобразование объекта TransactionRequestDto в объект Transaction")
    void testToEntity() {
        Long expectedPlayerId = 1L;
        TransactionRequestDto expectedTransaction =
                new TransactionRequestDto(1L, TransactionType.CREDIT, 0.0);
        Transaction actualTransaction = transactionMapper.toEntity(expectedTransaction, expectedPlayerId);

        Assertions.assertThat(actualTransaction).usingRecursiveComparison()
                .ignoringFields("playerId", "createdTime")
                .isEqualTo(expectedTransaction);
    }

    @Test
    @DisplayName("Преобразование объекта TransactionRequestDto в объект Transaction, когда TransactionRequestDto пустой")
    void testToEntityWhenDtoIsBlank() {
        Long expectedPlayerId = 1L;
        TransactionRequestDto expectedTransaction =
                new TransactionRequestDto(null, null, null);

        Transaction actualTransaction = transactionMapper.toEntity(expectedTransaction, expectedPlayerId);

        Assertions.assertThat(actualTransaction).usingRecursiveComparison()
                .ignoringFields("playerId", "createdTime")
                .isEqualTo(expectedTransaction);
    }

}