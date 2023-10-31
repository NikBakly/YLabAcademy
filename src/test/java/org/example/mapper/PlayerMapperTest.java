package org.example.mapper;

import org.assertj.core.api.Assertions;
import org.example.domain.dto.PlayerResponseDto;
import org.example.domain.model.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlayerMapperTest {

    @Test
    @DisplayName("Преобразование объекта Player в объект PlayerResponseDto")
    void testToResponseDto() {
        Player expectedPlayer = new Player(1L, "login", "password", 0.0);

        PlayerResponseDto actualPlayer = PlayerMapper.INSTANCE.toResponseDto(expectedPlayer);

        Assertions.assertThat(expectedPlayer.getId().equals(actualPlayer.id()) &&
                        expectedPlayer.getLogin().equals(actualPlayer.login()) &&
                        expectedPlayer.getBalance().equals(actualPlayer.balance()))
                .isTrue();
    }

}