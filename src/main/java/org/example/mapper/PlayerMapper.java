package org.example.mapper;

import org.example.dto.PlayerResponseDto;
import org.example.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Интерфейс для преобразования объектов Player и его DTO
 */
@Mapper
public interface PlayerMapper {
    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    PlayerResponseDto toResponseDto(Player player);
}
