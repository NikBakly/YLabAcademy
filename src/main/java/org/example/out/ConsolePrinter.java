package org.example.out;

import org.example.util.BasicPhrases;

//todo Удалить этот, клаасс
public class ConsolePrinter {
    public static ConsolePrinter instance;

    private ConsolePrinter() {
    }

    /**
     * Метод для реализации шаблона проектирования Singleton
     *
     * @return сущность ConsolePrinter
     */
    public static ConsolePrinter getInstance() {
        if (instance == null) {
            instance = new ConsolePrinter();
        }
        return instance;
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printMessage(BasicPhrases basicPhrases) {
        System.out.println(basicPhrases);
    }
}
