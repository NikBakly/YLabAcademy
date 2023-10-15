package org.example;

import org.example.in.ConsoleReader;
import org.example.in.contoller.PlayerConsoleController;
import org.example.service.AuditServiceImpl;
import org.example.service.PlayerServiceImpl;
import org.example.service.TransactionServiceImpl;

/**
 * Класс App является точкой входа
 */
public class App {
    public static void main(String[] args) throws Exception {
        new PlayerConsoleController(
                PlayerServiceImpl.getInstance(),
                AuditServiceImpl.getInstance(),
                TransactionServiceImpl.getInstance(),
                ConsoleReader.getInstance()
        ).start();
    }
}
