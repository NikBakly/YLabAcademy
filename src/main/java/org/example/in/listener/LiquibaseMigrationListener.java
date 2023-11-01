package org.example.in.listener;

import org.example.util.DatabaseConnector;
import org.example.util.LiquibaseManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
