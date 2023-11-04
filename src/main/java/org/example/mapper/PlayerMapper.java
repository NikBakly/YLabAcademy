package org.example.mapper;

import org.example.domain.dto.PlayerResponseDto;
import org.example.domain.model.Player;
import org.mapstruct.Mapper;

/**
 * Интерфейс для преобразования объектов Player и его DTO
 */
@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerResponseDto toResponseDto(Player player);
}
