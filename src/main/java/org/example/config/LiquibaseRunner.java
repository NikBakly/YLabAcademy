package org.example.config;

import org.example.util.LiquibaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LiquibaseRunner implements ApplicationRunner {
    private final LiquibaseManager liquibaseManager;

    @Autowired
    public LiquibaseRunner(LiquibaseManager liquibaseManager) {
        this.liquibaseManager = liquibaseManager;
    }

    @Override
    public void run(ApplicationArguments args) {
        liquibaseManager.runDatabaseMigrations();
    }
}
