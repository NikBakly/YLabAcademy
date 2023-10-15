package org.example.repository;

import org.assertj.core.api.Assertions;
import org.example.model.Audit;
import org.example.util.AuditType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Класс для тестирования AuditInMemoryRepository
 */
class AuditInMemoryRepositoryTest {
    AuditRepository repository;

    @BeforeEach
    void init() {
        repository = new AuditInMemoryRepository();
    }

    /**
     * Тест для проверки сохранений различных аудитов и последующего их нахождения по логину игрока.
     */
    @Test
    @DisplayName("Удачное создание аудитов и их нахождения по логину игрока")
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

        Assertions.assertThat(expectedAuditsSize)
                .as("Размеры полученных аудитов не совпадает с ожидаемым.")
                .isEqualTo(foundedAudits.size());
        // id для поэтапного вызова из списка полученных аудитов
        int nextId = 0;
        // Проверка, что результат получен в отсортированном по времени создания виде.
        Assertions.assertThat(AuditType.REGISTRATION)
                .as("Должна быть регистрация.")
                .isEqualTo(foundedAudits.get(nextId++).type());
        Assertions.assertThat(AuditType.BALANCE_REQUEST)
                .as("Должно быть действие запрос средств.")
                .isEqualTo(foundedAudits.get(nextId++).type());
        Assertions.assertThat(AuditType.EXIT)
                .as("Должно быть действие выход.")
                .isEqualTo(foundedAudits.get(nextId++).type());
        Assertions.assertThat(AuditType.AUTHORIZATION)
                .as("Должна быть авторизация.")
                .isEqualTo(foundedAudits.get(nextId++).type());
        Assertions.assertThat(AuditType.CREDIT)
                .as("Должно быть действие пополнение средств.")
                .isEqualTo(foundedAudits.get(nextId++).type());
        Assertions.assertThat(AuditType.DEBIT)
                .as("Должно быть действие снятие средств.")
                .isEqualTo(foundedAudits.get(nextId++).type());
        Assertions.assertThat(AuditType.REQUEST_DEBIT_HISTORY)
                .as("Должно быть действие запрос истории снятия средств.")
                .isEqualTo(foundedAudits.get(nextId++).type());
        Assertions.assertThat(AuditType.REQUEST_CREDIT_HISTORY)
                .as("Должно быть действие запрос истории пополнения средств.")
                .isEqualTo(foundedAudits.get(nextId++).type());
        Assertions.assertThat(AuditType.ERROR_ENTERING_COMMAND)
                .as("Должно быть действие неправильного ввода команды.")
                .isEqualTo(foundedAudits.get(nextId).type());
    }

}