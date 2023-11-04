package org.example.mapper;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.PlayerResponseDto;
import org.example.domain.model.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class PlayerMapperTest {
    private final PlayerMapper playerMapper = new PlayerMapperImpl();

    @Test
    @DisplayName("Преобразование объекта Player в объект PlayerResponseDto")
    void testToResponseDto() {
        Player expectedPlayer = new Player(1L, "login", "password", BigDecimal.ZERO);
        PlayerResponseDto actualPlayer = playerMapper.toResponseDto(expectedPlayer);

        Assertions.assertThat(actualPlayer).usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("password")
                .isEqualTo(expectedPlayer);
    }

    @Test
    @DisplayName("Преобразование объекта Player в объект PlayerResponseDto, когда Player пустой")
    void testToResponseDtoWhenEntityIsBlank() {
        Player expectedPlayer = new Player(null, null, null, null);

        PlayerResponseDto actualPlayer = playerMapper.toResponseDto(expectedPlayer);

        Assertions.assertThat(actualPlayer).usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("password")
                .isEqualTo(expectedPlayer);
    }

}