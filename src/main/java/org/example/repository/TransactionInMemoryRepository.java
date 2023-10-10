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

public class TransactionInMemoryRepository {
    private static TransactionInMemoryRepository instance;

    /**
     * Коллекция для хранения транзакций по их id
     */
    private final Map<Long, Transaction> transactions;

    private TransactionInMemoryRepository() {
        transactions = new HashMap<>();
    }

    /**
     * Метод для реализации шаблона проектирования Singleton
     *
     * @return сущность TransactionInMemoryRepository
     */
    public static TransactionInMemoryRepository getInstance() {
        if (instance == null) {
            instance = new TransactionInMemoryRepository();
        }
        return instance;
    }

    public void createdTransaction(Long transactionId, TransactionType type, Double size, String loginPlayer) throws SaveEntityException {
        if (transactions.containsKey(transactionId)) {
            throw new SaveEntityException("Id транзакции не является уникальным!");
        }
        Transaction newTransaction = new Transaction(transactionId, type, size, loginPlayer, Instant.now());
        transactions.put(transactionId, newTransaction);
    }

    public List<Transaction> getCreditHistoryTransactionsByCreatedTime(String login) {
        return transactions.values().stream()
                .filter(transaction -> transaction.loginPlayer().equals(login))
                .filter(transaction -> transaction.type().equals(TransactionType.CREDIT))
                .sorted(Comparator.comparing(Transaction::createdTime))
                .collect(Collectors.toList());
    }

    public List<Transaction> getDebitHistoryTransactionsByCreatedTime(String login) {
        return transactions.values().stream()
                .filter(transaction -> transaction.loginPlayer().equals(login))
                .filter(transaction -> transaction.type().equals(TransactionType.DEBIT))
                .sorted(Comparator.comparing(Transaction::createdTime))
                .collect(Collectors.toList());
    }

}
