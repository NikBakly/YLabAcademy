package org.example.domain.dto;

import java.math.BigDecimal;

/**
 * Класс для передачи данных об игроке
 *
 * @param id      идентификатор игрока
 * @param login   логин игрока
 * @param balance баланс игрока
 */
public record PlayerResponseDto(Long id,
                                String login,
                                BigDecimal balance) {
}
