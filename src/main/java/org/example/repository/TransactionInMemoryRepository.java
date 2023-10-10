package org.example.repository;

import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.util.TransactionType;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс для хранения сущности Transaction в памяти компьютера.
 */
public class TransactionInMemoryRepository {
    private static TransactionInMemoryRepository instance;

    /**
     * Коллекция для хранения транзакций по их id.
     */
    private final Map<Long, Transaction> transactions;

    public TransactionInMemoryRepository() {
        transactions = new HashMap<>();
    }


    /**
     * Метод для создания транзакции.
     *
     * @param transactionId   уникальное id транзакции
     * @param transactionType тип транзакции
     * @param size            размер транзакции
     * @param loginPlayer     логин игрока
     * @throws SaveEntityException ошибка при попытке создания транзакции
     */
    public void createdTransaction(Long transactionId,
                                   TransactionType transactionType,
                                   Double size,
                                   String loginPlayer) throws SaveEntityException {
        if (transactions.containsKey(transactionId)) {
            throw new SaveEntityException("Id транзакции не является уникальным!");
        }
        Transaction newTransaction = new Transaction(transactionId, transactionType, size, loginPlayer, Instant.now());
        transactions.put(transactionId, newTransaction);
    }

    /**
     * Метод для нахождения всех транзакций типа CREDIT по логину игрока и отсортированный по времени.
     *
     * @param loginPlayer логин игрока
     * @return список всех транзакций типа CREDIT по логину игрока и отсортированный по времени
     */
    public List<Transaction> findCreditHistoryTransactionsByCreatedTime(String loginPlayer) {
        return transactions.values().stream()
                .filter(transaction -> transaction.loginPlayer().equals(loginPlayer))
                .filter(transaction -> transaction.type().equals(TransactionType.CREDIT))
                .sorted(Comparator.comparing(Transaction::createdTime))
                .collect(Collectors.toList());
    }

    /**
     * Метод для нахождения всех транзакций типа DEBIT по логину игрока и отсортированный по времени.
     *
     * @param loginPlayer логин игрока
     * @return список всех транзакций типа DEBIT по логину игрока и отсортированный по времени
     */
    public List<Transaction> findDebitHistoryTransactionsByCreatedTime(String loginPlayer) {
        return transactions.values().stream()
                .filter(transaction -> transaction.loginPlayer().equals(loginPlayer))
                .filter(transaction -> transaction.type().equals(TransactionType.DEBIT))
                .sorted(Comparator.comparing(Transaction::createdTime))
                .collect(Collectors.toList());
    }

}
