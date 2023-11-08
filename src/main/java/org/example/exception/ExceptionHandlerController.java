package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Контроллер для отлавливания исключений и последующем ответом на HTTP-запрос
 */
@RestControllerAdvice
public class ExceptionHandlerController {

    /**
     * Метод для отлавливания NotFoundException и возвращает
     * объект, содержащий информацию об ошибки
     *
     * @param e отловленная ошибка
     * @return объект, содержащий информацию об ошибки
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    ApiError handleNotFoundException(NotFoundException e) {
        return new ApiError(e.getMessage());
    }

    /**
     * Метод для отлавливания InvalidInputException, SaveEntityException и возвращает
     * объект, содержащий информацию об ошибки
     *
     * @param e отловленная ошибка
     * @return объект, содержащий информацию об ошибки
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InvalidInputException.class, SaveEntityException.class})
    ApiError handleInvalidInputOrSaveEntityException(RuntimeException e) {
        return new ApiError(e.getMessage());
    }

    /**
     * Метод для отлавливания Exception и возвращает
     * объект, содержащий информацию об ошибки
     *
     * @param e отловленная ошибка
     * @return объект, содержащий информацию об ошибки
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    ApiError handleExceptionException(RuntimeException e) {
        return new ApiError(e.getMessage());
    }

}
