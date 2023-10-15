package org.example.exception;

/**
 * Класс отвечает за ошибки при неверных входных параметрах.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
