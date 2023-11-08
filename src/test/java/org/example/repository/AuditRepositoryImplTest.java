package org.example.repository;

import org.assertj.core.api.Assertions;
import org.example.domain.model.Audit;
import org.example.util.AuditType;
import org.example.util.DatabaseConnector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

/**
 * Класс для тестирования AuditInMemoryRepository
 */
@Testcontainers
@SpringBootTest
class AuditRepositoryImplTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName(DatabaseConnector.DATABASE_NAME)
            .withUsername(DatabaseConnector.USERNAME)
            .withPassword(DatabaseConnector.PASSWORD);

    @Autowired
    AuditRepository repository;

    @DynamicPropertySource
    static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    static void startContainer() {
        postgresContainer.start();
    }

    @AfterAll
    static void closeContainer() {
        postgresContainer.close();
    }

    /**
     * Тест для проверки сохранений различных аудитов и последующего их нахождения по логину игрока.
     */
    @Test
    @DisplayName("Удачное создание аудитов и их нахождения по идентификатору игрока")
    void testAddAndFindAuditsByLoginPlayer() {
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