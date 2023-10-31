package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.exception.SaveEntityException;
import org.example.model.Player;
import org.example.util.DatabaseConnector;

import java.sql.*;
import java.util.Optional;

/**
 * Класс отвечающий за взаимодействие сущности Player с БД
 */
public class PlayerRepositoryImpl implements PlayerRepository {
    private static final Logger log = LogManager.getLogger(PlayerRepositoryImpl.class);
    private static final String INSERT_SQL = "INSERT INTO wallet.players (login, password) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT * FROM wallet.players WHERE login = ?";
    private static final String UPDATE_BALANCE_SQL = "UPDATE wallet.players SET balance = ? WHERE login = ?";

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
    public PlayerRepositoryImpl(String jdbcUrl, String jdbcUsername, String jdbcPassword) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
    }

    /**
     * Берем стандартные настройки
     */
    public PlayerRepositoryImpl() {
        this.jdbcUrl = DatabaseConnector.URL;
        this.jdbcUsername = DatabaseConnector.USERNAME;
        this.jdbcPassword = DatabaseConnector.PASSWORD;
    }


    @Override
    public Player save(String loginPlayer, String password) throws SaveEntityException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
            preparedStatement.setString(1, loginPlayer);
            preparedStatement.setString(2, password);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 0) {
                log.warn("Игрок с login={} не сохранился.", loginPlayer);
            }
        } catch (SQLException e) {
            throw new SaveEntityException(e.getMessage());
        }
        return findByLogin(loginPlayer).orElseThrow(() -> new SaveEntityException("Сущность не сохранена"));
    }

    @Override
    public Optional<Player> findByLogin(String loginPlayer) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SQL)) {
            preparedStatement.setString(1, loginPlayer);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Long playerId = resultSet.getLong("id");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                Double balance = resultSet.getDouble("balance");
                Player foundPlayer = new Player(playerId, login, password, balance);
                return Optional.of(foundPlayer);
            }
        } catch (SQLException e) {
            log.warn(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void updateBalanceByLogin(String loginPlayer, double newBalance) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BALANCE_SQL)) {
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setString(2, loginPlayer);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                log.warn("У игрока с login={} не обновился баланс.", loginPlayer);
            }

        } catch (SQLException e) {
            log.warn(e.getMessage());
        }
    }
}
