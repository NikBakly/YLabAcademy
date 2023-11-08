package org.example.mapper;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.PlayerResponseDto;
import org.example.domain.model.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

/**
 * Класс для тестирования PlayerMapper
 */
@SpringBootTest
class PlayerMapperTest {

    @Autowired
    private PlayerMapper playerMapper;

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