package org.example.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Класс для чтения данных из консоли.
 * Имплементация AutoCloseable нужна для закрытия потока BufferReader
 */
public class ConsoleReader implements AutoCloseable {
    private static ConsoleReader instance;
    private final BufferedReader reader;

    private ConsoleReader() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Метод для реализации шаблона проектирования Singleton
     *
     * @return сущность ConsoleReader
     */
    public static ConsoleReader getInstance() {
        if (instance == null) {
            instance = new ConsoleReader();
        }
        return instance;
    }

    /**
     * Метод для определения типы операции из консоли
     *
     * @return вид операции
     * @throws IOException если возникла ошибка при чтении данных
     */
    public int readTypeOperation() throws IOException {
        return Integer.parseInt(reader.readLine());
    }

    /**
     * Метод для определения вводимой информации из консоли
     *
     * @return введенная информация
     * @throws IOException если возникла ошибка при чтении данных
     */
    public String readStringInfo() throws IOException {
        return reader.readLine();
    }

    public long readTransactionId() throws IOException {
        return Long.parseLong(reader.readLine());
    }

    public double readDoubleNumber() throws IOException {
        return Double.parseDouble(reader.readLine());
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
