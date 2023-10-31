package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.aop.annotations.LoggableService;
import org.example.domain.dto.TransactionResponseDto;
import org.example.domain.model.Transaction;
import org.example.exception.SaveEntityException;
import org.example.repository.TransactionRepository;
import org.example.repository.TransactionRepositoryImpl;
import org.example.util.TransactionType;

import java.util.List;

/**
 * Класс реализующий бизнес-логику для сущности Transaction.
 */
@LoggableService
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LogManager.getLogger(TransactionServiceImpl.class);
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
        log.info("Транзакция с id={} успешно создана.", newTransaction.id());
    }

    @Override
    public List<TransactionResponseDto> findHistoryTransactions(Long playerId, TransactionType transactionType) {
        List<TransactionResponseDto> foundHistoryTransactions =
                transactionRepository.findHistoryTransactionsByCreatedTime(playerId, transactionType);
        log.info("Истории транзакций успешно найдены.");
        return foundHistoryTransactions;
    }
}
