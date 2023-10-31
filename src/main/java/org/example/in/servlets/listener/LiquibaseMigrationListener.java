package org.example.in.servlets.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.util.DatabaseConnector;
import org.example.util.LiquibaseManager;

/**
 * Класс нужен для запуска миграции, при инициализации контекста веб-приложения
 */
@WebListener
public class LiquibaseMigrationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LiquibaseManager
                .runDatabaseMigrations(
                        DatabaseConnector.URL,
                        DatabaseConnector.USERNAME,
                        DatabaseConnector.PASSWORD);
    }
}
