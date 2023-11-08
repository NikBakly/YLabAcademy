package org.example.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Класс для ответа на HTTP-запрос аутентификации
 *
 * @param jwtToken jwt-токен
 */
public record ResponseJwtToken(
        @Schema(example = "eyJhbGczI1NiJ9.eyJzdWIiOi5MjkxMjYxfQ.fBw6-R-cxdxvJI3rUKGI") String jwtToken) {
}
