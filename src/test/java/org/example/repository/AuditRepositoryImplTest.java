package org.example.repository;

import org.assertj.core.api.Assertions;
import org.example.model.Audit;
import org.example.util.AuditType;
import org.example.util.DatabaseConnector;
import org.example.util.LiquibaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

/**
 * Класс для тестирования AuditInMemoryRepository
 */
@Testcontainers
class AuditRepositoryImplTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName(DatabaseConnector.DATABASE_NAME)
            .withUsername(DatabaseConnector.USERNAME)
            .withPassword(DatabaseConnector.PASSWORD);

    AuditRepository repository;

    @BeforeEach
    void init() {
        postgresContainer.start();
        LiquibaseManager.runDatabaseMigrations(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
        repository = new AuditRepositoryImpl(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );
    }

    @AfterEach
    void closeContainer() {
        postgresContainer.close();
    }

    /**
     * Тест для проверки сохранений различных аудитов и последующего их нахождения по логину игрока.
     */
    @Test
    @DisplayName("Удачное создание аудитов и их нахождения по идентификатору игрока")
    void addAndFindAuditsByLoginPlayer() {
        Long playerId = 1L;
        // заполняем различными действиями пользователя
        repository.createAudit(AuditType.REGISTRATION, playerId);
        repository.createAudit(AuditType.AUTHORIZATION, playerId);
        repository.createAudit(AuditType.CREDIT, playerId);
        repository.createAudit(AuditType.DEBIT, playerId);
        repository.createAudit(AuditType.REQUEST_DEBIT_HISTORY, playerId);
        repository.createAudit(AuditType.REQUEST_CREDIT_HISTORY, playerId);

        List<Audit> foundedAudits = repository.findAuditsByPlayerIdByCreatedTime(playerId);
        int expectedAuditsSize = 6;

        Assertions.assertThat(expectedAuditsSize)
                .as("Размеры полученных аудитов не совпадает с ожидаемым.")
                .isEqualTo(foundedAudits.size());
        // id для поэтапного вызова из списка полученных аудитов
        int nextId = 0;
        // Проверка, что результат получен в отсортированном по времени создания виде.
        Assertions.assertThat(AuditType.REGISTRATION)
                .as("Должна быть регистрация.")
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
                .isEqualTo(foundedAudits.get(nextId).type());
    }

}