package org.example.exception;

/**
 * Класс отвечает за ошибки при не нахождения нужной сущности
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
