package org.example.repository;

import org.example.model.Audit;
import org.example.util.AuditType;
import org.example.util.DatabaseConnector;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuditRepositoryImpl implements AuditRepository {
    private static final String INSERT_SQL =
            "INSERT INTO wallet.audits (type, login_player, created_time) VALUES (?, ?, ?)";
    private static final String SELECT_SQL =
            "SELECT * FROM wallet.audits WHERE login_player = ? ORDER BY created_time";


    private final String jdbcUrl;
    private final String jdbcUsername;
    private final String jdbcPassword;

    /**
     * Для тестирования
     *
     * @param jdbcUrl      url бд
     * @param jdbcUsername имя пользователя в бд
     * @param jdbcPassword пароль пользователя в бд
     */
    public AuditRepositoryImpl(String jdbcUrl, String jdbcUsername, String jdbcPassword) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
    }

    /**
     * Берем стандартные настройки
     */
    public AuditRepositoryImpl() {
        this.jdbcUrl = DatabaseConnector.URL;
        this.jdbcUsername = DatabaseConnector.USERNAME;
        this.jdbcPassword = DatabaseConnector.PASSWORD;
    }

    @Override
    public void addAudit(AuditType auditType, String loginPlayer) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
            preparedStatement.setString(1, auditType.toString());
            preparedStatement.setString(2, loginPlayer);
            preparedStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 0) {
                System.err.printf("У игрока с login=%s не сохранился аудит.%n", loginPlayer);
            }
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }
    }

    @Override
    public List<Audit> findAuditsByLoginPlayerByCreatedTime(String loginPlayer) {
        List<Audit> foundAudits = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SQL)) {
            preparedStatement.setString(1, loginPlayer);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long auditId = resultSet.getLong("id");
                AuditType auditType = resultSet.getString("type")
                        .transform(AuditType::valueOf);
                String auditLoginPlayer = resultSet.getString("login_player");
                Timestamp auditCreatedTime = resultSet.getTimestamp("created_time");
                foundAudits.add(new Audit(
                        auditId,
                        auditType,
                        auditLoginPlayer,
                        auditCreatedTime.toInstant()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundAudits;
    }
}
