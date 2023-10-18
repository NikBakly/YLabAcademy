package org.example.util;

import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

/**
 * Класс управляет миграциями с использованием Liquibase
 */
public class LiquibaseManager {
    /**
     * Метод запускает миграции базы данных
     */
    public static void runDatabaseMigrations(String url, String username, String password) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String propertiesFile = "liquibase/liquibase.properties";
            Properties properties = new Properties();
            properties.load(classLoader.getResourceAsStream(propertiesFile));


            Connection connection = DriverManager.getConnection(url, username, password);
            createSchemaForLiquibaseTables(connection);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));


            database.setDefaultSchemaName(properties.getProperty("defaultSchemaName"));

            CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
            updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database);
            updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, properties.getProperty("changeLogFile"));
            updateCommand.execute();


            connection.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Метод для создания схемы в бд, которая хранить таблицы liquibase
     *
     * @param connection подключение к бд
     */
    private static void createSchemaForLiquibaseTables(Connection connection) {
        String createSchemaSQL = "CREATE SCHEMA migration";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(createSchemaSQL);
            statement.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


}
