package org.example.util;

public enum BasicPhrases {
    FOR_UNAUTHORIZED("""
            Приветствует НИК - управленец твоими транзакциями!
            Доступны следующие команды:
            \t0.Завершить работу;
            \t1.Регистрация;
            \t2.Авторизация."""),
    FOR_AUTHORIZED("""
            Вы находитесь в личном кабинете!
            Доступны следующие команды:
            \t1.Выйти и зарегистрировать;
            \t2.Выйти и авторизация;
            \t3.Текущий ваш баланс;
            \t4.Дебет/снятие средств;
            \t5.Кредит на игрока;
            \t6.Просмотр истории пополнений средств;
            \t7.Просмотр истории снятия средств;
            \t8.Просмотр своего аудита."""),
    REQUEST_LOGIN_AND_PASSWORD("""
            Введите уникальный логин и пароль через разрыв строки. Вот пример:
            yourLogin
            yourPassword"""),
    COMMAND_NOT_FOUND("Вы ввели не существующие команду!"),
    ERROR_READING_FROM_CONSOLE("Ошибка при чтении с консоли."),
    AKS_FOR_TRANSACTION_ID("Введите уникальный идентификатор транзакции:"),
    ASK_FOR_DEBIT_SIZE("Какое количество средств хотите списать ? Введите число:"),
    ASK_FOR_CREDIT_SIZE("На какое количество средств хотите пополнить ? Введите число:"),
    SUCCESSFUL_OPERATION("Операция выполнена успешно!"),
    GOODBYE("Спасибо, что вы были с нами. До свидания!");

    private final String command;

    BasicPhrases(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }
}
