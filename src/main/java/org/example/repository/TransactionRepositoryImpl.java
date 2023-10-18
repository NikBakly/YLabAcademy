package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.util.DatabaseConnector;
import org.example.util.TransactionType;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryImpl implements TransactionRepository {
    private static final String INSERT_SQL =
            "INSERT INTO wallet.transactions (id, type, size, login_player, created_time) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_CREDIT_SQL =
            "SELECT * FROM wallet.transactions WHERE login_player = ? AND type = ? ORDER BY created_time";

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
    public void createdTransaction(Long transactionId,
                                   TransactionType transactionType,
                                   Double transactionSize,
                                   String loginPlayer) throws SaveEntityException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
            preparedStatement.setLong(1, transactionId);
            preparedStatement.setString(2, transactionType.toString());
            preparedStatement.setDouble(3, transactionSize);
            preparedStatement.setString(4, loginPlayer);
            preparedStatement.setTimestamp(5, Timestamp.from(Instant.now()));
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 0) {
                System.err.printf("Транзакция id=%d не сохранилась.%n", transactionId);
            }
        } catch (SQLException e) {
            throw new SaveEntityException(e.getMessage());
        }
    }

    @Override
    public List<Transaction> findHistoryTransactionsByCreatedTime(String loginPlayer, TransactionType transactionType) {
        List<Transaction> foundTransactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CREDIT_SQL)) {
            preparedStatement.setString(1, loginPlayer);
            preparedStatement.setString(2, transactionType.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long transactionId = resultSet.getLong("id");
                TransactionType type = resultSet.getString("type")
                        .transform(TransactionType::valueOf);
                Double transactionSize = resultSet.getDouble("size");
                String transactionLoginPlayer = resultSet.getString("login_player");
                Timestamp transactionCreatedTime = resultSet.getTimestamp("created_time");
                foundTransactions.add(new Transaction(
                        transactionId,
                        type,
                        transactionSize,
                        transactionLoginPlayer,
                        transactionCreatedTime.toInstant()
                ));
            }
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }

        return foundTransactions;
    }
}
