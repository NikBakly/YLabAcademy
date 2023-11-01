package org.example.domain.dto;

/**
 * Класс для ответа на HTTP-запрос аутентификации
 *
 * @param jwtToken jwt-токен
 */
public record ResponseJwtToken(String jwtToken) {
}
