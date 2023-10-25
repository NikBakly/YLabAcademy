package org.example.util;

import java.nio.charset.StandardCharsets;

/**
 * Класс для хранения основной информации для генерации и проверки jwt токенов
 */
public class JwtUtil {
    /**
     * Секретное слов при генерации jwt токена
     */
    public static final byte[] secret = "ylabCompany".getBytes(StandardCharsets.UTF_8);

    /**
     * Один час в миллисекундах для ограничения хранения токена по времени
     */
    public static final int oneHourInMilliseconds = 3600000;
}
