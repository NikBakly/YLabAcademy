package org.example.service;

import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.repository.TransactionInMemoryRepository;
import org.example.util.TransactionType;

import java.util.List;

/**
 * Класс ответственный за бизнес-логику для сущности Transaction
 */
public class TransactionService {
    private static TransactionService instance;

    private final TransactionInMemoryRepository transactionInMemoryRepository;

    private TransactionService() {
        this.transactionInMemoryRepository = TransactionInMemoryRepository.getInstance();
    }

    public TransactionService(TransactionInMemoryRepository transactionInMemoryRepository) {
        this.transactionInMemoryRepository = transactionInMemoryRepository;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton
     *
     * @return сущность TransactionService
     */
    public static TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }

    public void createTransaction(Long transactionId, TransactionType type, Double size, String loginPlayer) throws SaveEntityException {
        transactionInMemoryRepository.createdTransaction(transactionId, type, size, loginPlayer);
    }

    public List<Transaction> getCreditHistoryTransactions(String login) {
        return transactionInMemoryRepository.getCreditHistoryTransactionsByCreatedTime(login);
    }

    public List<Transaction> getDebitHistoryTransactions(String login) {
        return transactionInMemoryRepository.getDebitHistoryTransactionsByCreatedTime(login);
    }
}
