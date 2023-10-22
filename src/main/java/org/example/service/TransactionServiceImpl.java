package org.example.service;

import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.repository.TransactionRepository;
import org.example.repository.TransactionRepositoryImpl;
import org.example.util.TransactionType;

import java.util.List;

/**
 * Класс реализующий бизнес-логику для сущности Transaction.
 */
public class TransactionServiceImpl implements TransactionService {
    private static TransactionServiceImpl instance;

    private final TransactionRepository transactionRepository;

    private TransactionServiceImpl() {
        this.transactionRepository = new TransactionRepositoryImpl();
    }

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton.
     *
     * @return сущность TransactionService
     */
    public static TransactionServiceImpl getInstance() {
        if (instance == null) {
            instance = new TransactionServiceImpl();
        }
        return instance;
    }

    @Override
    public void createTransaction(Transaction newTransaction) throws SaveEntityException {
        transactionRepository.createdTransaction(newTransaction);
    }

    @Override
    public List<Transaction> getHistoryTransactions(Long playerId, TransactionType transactionType) {
        return transactionRepository.findHistoryTransactionsByCreatedTime(playerId, transactionType);
    }

}
