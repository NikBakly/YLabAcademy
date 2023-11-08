package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.Audit;
import org.example.util.AuditType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс отвечающий за взаимодействие сущности Audit с БД
 */
@Repository
public class AuditRepositoryImpl implements AuditRepository {
    private static final Logger log = LogManager.getLogger(AuditRepositoryImpl.class);
    private static final String INSERT_SQL =
            "INSERT INTO wallet.audits (type, player_id, created_time) VALUES (?, ?, ?)";
    private static final String SELECT_SQL =
            "SELECT * FROM wallet.audits WHERE player_id = ? ORDER BY created_time";

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String jdbcUsername;

    @Value("${spring.datasource.password}")
    private String jdbcPassword;


    @Override
    public void createAudit(AuditType auditType, Long playerId) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
            preparedStatement.setString(1, auditType.toString());
            preparedStatement.setLong(2, playerId);
            preparedStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 0) {
                log.warn("У игрока с login={} не сохранился аудит.", playerId);
            }
        } catch (SQLException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public List<Audit> findAuditsByPlayerIdByCreatedTime(Long playerId) {
        List<Audit> foundAudits = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SQL)) {
            preparedStatement.setLong(1, playerId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long auditId = resultSet.getLong("id");
                AuditType auditType = resultSet.getString("type")
                        .transform(AuditType::valueOf);
                Long auditPlayerId = resultSet.getLong("player_id");
                Timestamp auditCreatedTime = resultSet.getTimestamp("created_time");
                foundAudits.add(new Audit(
                        auditId,
                        auditType,
                        auditPlayerId,
                        auditCreatedTime.toInstant()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundAudits;
    }
}