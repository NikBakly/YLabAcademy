package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.aop.annotations.LoggableService;
import org.example.domain.dto.TransactionResponseDto;
import org.example.domain.model.Transaction;
import org.example.exception.SaveEntityException;
import org.example.repository.TransactionRepository;
import org.example.util.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Класс реализующий бизнес-логику для сущности Transaction.
 */
@Service
@LoggableService
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LogManager.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
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
