package org.example.mapper;

import org.example.domain.dto.TransactionRequestDto;
import org.example.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

/**
 * Интерфейс для преобразования объектов Transaction и его DTO
 */
@Mapper(componentModel = "spring", imports = Instant.class)
public interface TransactionMapper {

    default Transaction toEntity(TransactionRequestDto transactionRequestDto, Long playerId) {
        if (transactionRequestDto == null || playerId == null) {
            return null;
        }
        return toEntityInternal(transactionRequestDto, playerId);
    }

    @Mapping(target = "createdTime", expression = "java(Instant.now())")
    @Mapping(source = "playerId", target = "playerId")
    Transaction toEntityInternal(TransactionRequestDto transactionRequestDto, Long playerId);

}
