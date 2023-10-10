package org.example;

import org.example.in.contoller.PlayerConsoleController;

/**
 * Класс app является точкой входа
 */
public class App {
    public static void main(String[] args) throws Exception {
        PlayerConsoleController.getInstance().start();
    }
}
