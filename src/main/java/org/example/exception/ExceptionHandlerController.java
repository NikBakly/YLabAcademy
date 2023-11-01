package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Контроллер для отлавливания исключений и последующем ответом на HTTP-запрос
 */
@RestControllerAdvice
public class ExceptionHandlerController {

    /**
     * Метод отлавливает NotFoundException ошибку и в ответ
     * возвращает словарь с сообщением об ошибки для дальнейшей сериализация в JSON для ответа на HTTP-запрос
     *
     * @param e отловленная ошибка
     * @return словарь с сообщением об ошибки
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    Map<String, String> handleNotFoundException(NotFoundException e) {
        return getMapWithErrorMessage(e.getMessage());
    }

    /**
     * Метод отлавливает InvalidInputException, SaveEntityException ошибки и в ответ
     * возвращает словарь с сообщением об ошибки для дальнейшей сериализация в JSON для ответа на HTTP-запрос
     *
     * @param e отловленная ошибка
     * @return словарь с сообщением об ошибки
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InvalidInputException.class, SaveEntityException.class})
    Map<String, String> handleInvalidInputOrSaveEntityException(RuntimeException e) {
        return getMapWithErrorMessage(e.getMessage());
    }

    /**
     * Метод отлавливает все ошибки Exception или его потомков и в ответ
     * возвращает словарь с сообщением об ошибки для дальнейшей сериализация в JSON для ответа на HTTP-запрос
     *
     * @param e отловленная ошибка
     * @return словарь с сообщением об ошибки
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    Map<String, String> handleExceptionException(RuntimeException e) {
        return getMapWithErrorMessage(e.getMessage());
    }

    /**
     * Метод возвращает словарь, состоящею из базовой фразы и сообщении об ошибки
     *
     * @param errorMessage сообщение об ошибки
     * @return словарь с сообщением об ошибки
     */
    private Map<String, String> getMapWithErrorMessage(String errorMessage) {
        return Map.of("ErrorMessage", errorMessage);
    }
}
