package org.example.mapper;

import org.example.dto.TransactionRequestDto;
import org.example.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

/**
 * Интерфейс для преобразования объектов Transaction и его DTO
 */
@Mapper(imports = Instant.class)
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "createdTime", expression = "java(Instant.now())")
    @Mapping(source = "playerId", target = "playerId")
    Transaction toEntity(TransactionRequestDto transactionRequestDto, Long playerId);

}
