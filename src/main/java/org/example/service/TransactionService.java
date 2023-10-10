package org.example.service;

import org.example.exception.SaveEntityException;
import org.example.model.Transaction;
import org.example.repository.TransactionInMemoryRepository;
import org.example.util.TransactionType;

import java.util.List;

/**
 * Класс ответственный за бизнес-логику для сущности Transaction.
 */
public class TransactionService {
    private static TransactionService instance;

    private final TransactionInMemoryRepository transactionInMemoryRepository;

    private TransactionService() {
        this.transactionInMemoryRepository = new TransactionInMemoryRepository();
    }

    public TransactionService(TransactionInMemoryRepository transactionInMemoryRepository) {
        this.transactionInMemoryRepository = transactionInMemoryRepository;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton.
     *
     * @return сущность TransactionService
     */
    public static TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
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
    public void createTransaction(Long transactionId,
                                  TransactionType transactionType,
                                  Double size,
                                  String loginPlayer) throws SaveEntityException {
        transactionInMemoryRepository.createdTransaction(transactionId, transactionType, size, loginPlayer);
    }

    /**
     * Метод запрашивает все транзакций типа CREDIT по логину игрока и отсортированный по времени у репозитория.
     *
     * @param loginPlayer логин игрока
     * @return список всех транзакций типа CREDIT по логину игрока и отсортированный по времени
     */
    public List<Transaction> getCreditHistoryTransactions(String loginPlayer) {
        return transactionInMemoryRepository.findCreditHistoryTransactionsByCreatedTime(loginPlayer);
    }

    /**
     * Метод запрашивает все транзакций типа DEBIT по логину игрока и отсортированный по времени у репозитория.
     *
     * @param loginPlayer логин игрока
     * @return список всех транзакций типа DEBIT по логину игрока и отсортированный по времени
     */
    public List<Transaction> getDebitHistoryTransactions(String loginPlayer) {
        return transactionInMemoryRepository.findDebitHistoryTransactionsByCreatedTime(loginPlayer);
    }
}
