package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.dto.TransactionResponseDto;
import org.example.domain.model.Transaction;
import org.example.exception.SaveEntityException;
import org.example.util.DatabaseConnector;
import org.example.util.TransactionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс отвечающий за взаимодействие сущности Transaction с БД
 */
public class TransactionRepositoryImpl implements TransactionRepository {
    private static final Logger log = LogManager.getLogger(TransactionRepositoryImpl.class);
    private static final String INSERT_SQL =
            "INSERT INTO wallet.transactions (id, type, size, player_id, created_time) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_SQL =
            "SELECT t.id, t.type, t.size, t.created_time, p.login " +
                    "FROM wallet.transactions AS t " +
                    "JOIN wallet.players AS p ON t.player_id = p.id " +
                    "WHERE player_id = ? AND type = ? " +
                    "ORDER BY created_time";

    private final String jdbcUrl;
    private final String jdbcUsername;
    private final String jdbcPassword;

    public TransactionRepositoryImpl(String jdbcUrl, String jdbcUsername, String jdbcPassword) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
    }

    public TransactionRepositoryImpl() {
        this.jdbcUrl = DatabaseConnector.URL;
        this.jdbcUsername = DatabaseConnector.USERNAME;
        this.jdbcPassword = DatabaseConnector.PASSWORD;
    }

    @Override
    public void createdTransaction(Transaction newTransaction) throws SaveEntityException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
            preparedStatement.setLong(1, newTransaction.id());
            preparedStatement.setString(2, newTransaction.type().toString());
            preparedStatement.setDouble(3, newTransaction.size());
            preparedStatement.setLong(4, newTransaction.playerId());
            preparedStatement.setTimestamp(5, Timestamp.from(newTransaction.createdTime()));
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 0) {
                log.warn("Транзакция id={} не сохранилась.", newTransaction.id());
            }
        } catch (SQLException e) {
            throw new SaveEntityException(e.getMessage());
        }
    }

    @Override
    public List<TransactionResponseDto> findHistoryTransactionsByCreatedTime(Long playerId, TransactionType transactionType) {
        List<TransactionResponseDto> foundTransactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SQL)) {
            preparedStatement.setLong(1, playerId);
            preparedStatement.setString(2, transactionType.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long transactionId = resultSet.getLong("id");
                TransactionType type = resultSet.getString("type")
                        .transform(TransactionType::valueOf);
                Double transactionSize = resultSet.getDouble("size");
                String transactionLoginPlayer = resultSet.getString("login");
                Timestamp transactionCreatedTime = resultSet.getTimestamp("created_time");
                foundTransactions.add(new TransactionResponseDto(
                        transactionId,
                        type,
                        transactionSize,
                        transactionLoginPlayer,
                        transactionCreatedTime.toInstant()
                ));
            }
        } catch (SQLException e) {
            log.warn(e.getMessage());
        }

        return foundTransactions;
    }
}
