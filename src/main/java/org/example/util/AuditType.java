package org.example.util;

/**
 * Этот enum хранит основные тип аудитов
 */
public enum AuditType {
    REGISTRATION,
    AUTHORIZATION,
    EXIT,
    ERROR_ENTERING_COMMAND,
    CREDIT,
    DEBIT,
    BALANCE_REQUEST,
    REQUEST_CREDIT_HISTORY,
    REQUEST_DEBIT_HISTORY,
}
