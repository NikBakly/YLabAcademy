package org.example.exception;

/**
 * Класс для ответа на HTTP-запросы при ошибках
 *
 * @param errorMessage сообщение об ошибки
 */
public record ApiError(String errorMessage) {
}
