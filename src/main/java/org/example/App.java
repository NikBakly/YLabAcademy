package org.example;

import org.example.in.ConsoleReader;
import org.example.in.contoller.PlayerConsoleController;
import org.example.service.AuditServiceImpl;
import org.example.service.PlayerServiceImpl;
import org.example.service.TransactionServiceImpl;
import org.example.util.DatabaseConnector;
import org.example.util.LiquibaseManager;

/**
 * Класс App является точкой входа
 */
public class App {
    public static void main(String[] args) throws Exception {
        LiquibaseManager
                .runDatabaseMigrations(
                        DatabaseConnector.URL,
                        DatabaseConnector.USERNAME,
                        DatabaseConnector.PASSWORD);
        new PlayerConsoleController(
                PlayerServiceImpl.getInstance(),
                AuditServiceImpl.getInstance(),
                TransactionServiceImpl.getInstance(),
                ConsoleReader.getInstance()
        ).start();
    }
}
