package org.example.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Класс для чтения данных из консоли.
 * Имплементация AutoCloseable нужна для автоматического закрытия в try-with-resources потока BufferReader.
 */
public class ConsoleReader implements AutoCloseable {
    private static ConsoleReader instance;
    private final BufferedReader reader;

    private ConsoleReader() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Метод для реализации шаблона проектирования Singleton.
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
     * Метод для чтения типа операции из консоли.
     *
     * @return вид операции
     * @throws IOException ошибка при чтении данных
     */
    public int readTypeOperation() throws IOException {
        return Integer.parseInt(reader.readLine());
    }

    /**
     * Метод для чтения информации из консоли.
     *
     * @return информация
     * @throws IOException ошибка при чтении данных
     */
    public String readStringInfo() throws IOException {
        return reader.readLine();
    }

    /**
     * Метод для чтения идентификатора транзакции.
     *
     * @return идентификатор транзакции
     * @throws IOException ошибка при чтении данных
     */
    public long readTransactionId() throws IOException {
        return Long.parseLong(reader.readLine());
    }

    /**
     * Метод для чтения числа с плавающей точкой.
     *
     * @return число с плавающей точкой
     * @throws IOException ошибка при чтении данных
     */
    public double readDoubleNumber() throws IOException {
        return Double.parseDouble(reader.readLine());
    }

    /**
     * Метод для закрытия потока BufferReader.
     *
     * @throws Exception ошибка при попытке закрытия потока
     */
    @Override
    public void close() throws Exception {
        reader.close();
    }
}
