package org.example.repository;

import org.example.model.Audit;
import org.example.util.AuditType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Класс для тестирования AuditInMemoryRepository
 */
class AuditInMemoryRepositoryTest {
    static AuditInMemoryRepository repository;

    @BeforeAll
    static void init() {
        repository = AuditInMemoryRepository.getInstance();
    }

    /**
     * Тест для проверки шаблона проектирования Singleton
     */
    @Test
    @DisplayName("Тест 1. Проверка шаблона проектирования Singleton.")
    void getInstance() {
        AuditInMemoryRepository secondPointer = AuditInMemoryRepository.getInstance();
        Assertions.assertSame(repository, secondPointer, "Указатели ссылаются на разные объекты.");
    }

    /**
     * Тест для проверки сохранений различных аудитов и последующего их нахождения по логину игрока.
     */
    @Test
    @DisplayName("Тест 2. Проверка создания аудитов и их нахождения по логину игрока")
    void addAndFindAuditsByLoginPlayer() {
        String loginPlayer = "tester";
        // заполняем различными действиями пользователя
        repository.addAudit(AuditType.REGISTRATION, loginPlayer);
        repository.addAudit(AuditType.BALANCE_REQUEST, loginPlayer);
        repository.addAudit(AuditType.EXIT, loginPlayer);
        repository.addAudit(AuditType.AUTHORIZATION, loginPlayer);
        repository.addAudit(AuditType.CREDIT, loginPlayer);
        repository.addAudit(AuditType.DEBIT, loginPlayer);
        repository.addAudit(AuditType.REQUEST_DEBIT_HISTORY, loginPlayer);
        repository.addAudit(AuditType.REQUEST_CREDIT_HISTORY, loginPlayer);
        repository.addAudit(AuditType.ERROR_ENTERING_COMMAND, loginPlayer);

        List<Audit> foundedAudits = repository.findAuditsByLoginPlayerByCreatedTime(loginPlayer);
        int expectedAuditsSize = 9;

        Assertions.assertEquals(expectedAuditsSize, foundedAudits.size(),
                "Размеры полученных аудитов не совпадает с ожидаемым.");
        // id для поэтапного вызова из списка полученных аудитов
        int nextId = 0;
        // Проверка, что результат получен в отсортированном по времени создания виде.
        Assertions.assertEquals(AuditType.REGISTRATION, foundedAudits.get(nextId++).type(),
                "Должна быть регистрация.");
        Assertions.assertEquals(AuditType.BALANCE_REQUEST, foundedAudits.get(nextId++).type(),
                "Должно быть действие запрос средств.");
        Assertions.assertEquals(AuditType.EXIT, foundedAudits.get(nextId++).type(),
                "Должно быть действия выход.");
        Assertions.assertEquals(AuditType.AUTHORIZATION, foundedAudits.get(nextId++).type(),
                "Должна быть авторизация.");
        Assertions.assertEquals(AuditType.CREDIT, foundedAudits.get(nextId++).type(),
                "Должно быть действие пополнение средств.");
        Assertions.assertEquals(AuditType.DEBIT, foundedAudits.get(nextId++).type(),
                "Должно быть действие снятие средств.");
        Assertions.assertEquals(AuditType.REQUEST_DEBIT_HISTORY, foundedAudits.get(nextId++).type(),
                "Должно быть действие запрос истории снятия средств.");
        Assertions.assertEquals(AuditType.REQUEST_CREDIT_HISTORY, foundedAudits.get(nextId++).type(),
                "Должно быть действие запрос истории пополнения средств.");
        Assertions.assertEquals(AuditType.ERROR_ENTERING_COMMAND, foundedAudits.get(nextId).type(),
                "Должно быть действие неправильного ввода команды.");
    }

}