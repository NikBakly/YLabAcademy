package org.example.exception;

/**
 * Класс отвечает за ошибки во время попытки сохранения сущностей
 */
public class SaveEntityException extends RuntimeException {
    public SaveEntityException(String message) {
        super(message);
    }
}
