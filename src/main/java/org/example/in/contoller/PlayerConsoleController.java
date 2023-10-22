package org.example.in.contoller;

import org.example.exception.InvalidInputException;
import org.example.exception.SaveEntityException;
import org.example.in.ConsoleReader;
import org.example.model.Player;
import org.example.service.AuditService;
import org.example.service.PlayerService;
import org.example.service.TransactionService;
import org.example.util.AuditType;
import org.example.util.BasicPhrases;
import org.example.util.TransactionType;

/**
 * Класс, который ответственный за обработку поступающих запросов.
 */
public class PlayerConsoleController {

    private static final int EXIT_CODE = 0;
    private static final int REGISTRATION_CODE = 1;
    private static final int AUTHORIZATION_CODE = 2;
    private static final int BALANCE_CODE = 3;
    private static final int DEBIT_CODE = 4;
    private static final int CREDIT_CODE = 5;
    private static final int DEBIT_HISTORY_CODE = 6;
    private static final int CREDIT_HISTORY_CODE = 7;
    private static final int AUDIT_CODE = 8;

    private final PlayerService playerService;
    private final AuditService auditService;
    private final TransactionService transactionService;
    private final ConsoleReader consoleReader;

    /**
     * Поле для отслеживания окончания обработки пользовательских запросов.
     */
    private boolean isFinish = false;
    /**
     * Поле для отслеживания авторизованного игрока.
     */
    private Player playerNow = null;

    public PlayerConsoleController(PlayerService playerService,
                                   AuditService auditService,
                                   TransactionService transactionService,
                                   ConsoleReader consoleReader) {
        this.playerService = playerService;
        this.auditService = auditService;
        this.transactionService = transactionService;
        this.consoleReader = consoleReader;
    }


    /**
     * Метод запускающий цикл для обработки запросов пользователя.
     */
    public void start() throws Exception {
        while (!isFinish) {
            try {
                if (playerNow == null) {
                    processUnauthorizedPlayer();
                } else {
                    processAuthorizedPlayer();
                }
            } catch (InvalidInputException | SaveEntityException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(BasicPhrases.ERROR_READING_FROM_CONSOLE);
            }
        }
        System.out.println(BasicPhrases.GOODBYE);
        consoleReader.close();
    }

    /**
     * Метод для обработки не авторизованного игрока.
     *
     * @throws Exception отслеживания ошибка при обработке
     */
    private void processUnauthorizedPlayer() throws Exception {
        System.out.println(BasicPhrases.FOR_UNAUTHORIZED);
        int typeOperation = consoleReader.readTypeOperation();
        switch (typeOperation) {
            case EXIT_CODE -> isFinish = true;
            case REGISTRATION_CODE -> registerPlayer();
            case AUTHORIZATION_CODE -> authenticatePlayer();
            default -> System.out.println(BasicPhrases.COMMAND_NOT_FOUND);
        }
    }

    /**
     * Метод для обработки авторизованного игрока.
     *
     * @throws Exception отслеживания ошибка при обработке
     */
    private void processAuthorizedPlayer() throws Exception {
        System.out.println(BasicPhrases.FOR_AUTHORIZED);
        int typeOperation = consoleReader.readTypeOperation();
        switch (typeOperation) {
            case EXIT_CODE -> {
                auditService.addAudit(AuditType.EXIT, playerNow.getId());
                isFinish = true;
            }
            case REGISTRATION_CODE -> registerPlayer();
            case AUTHORIZATION_CODE -> authenticatePlayer();
            case BALANCE_CODE -> printBalancePlayer();
            case DEBIT_CODE -> debitForPlayer();
            case CREDIT_CODE -> creditForPlayer();
            case CREDIT_HISTORY_CODE -> printCreditHistory();
            case DEBIT_HISTORY_CODE -> printDebitHistory();
            case AUDIT_CODE -> printAudit();
            default -> {
                auditService.addAudit(AuditType.ERROR_ENTERING_COMMAND, playerNow.getId());
                System.out.println(BasicPhrases.COMMAND_NOT_FOUND);
            }

        }
    }

    /**
     * Метод для регистрации игрока.
     *
     * @throws Exception ошибка при попытке регистрации
     */
    private void registerPlayer() throws Exception {
        Long previousPlayerId = null;
        if (playerNow != null) {
            previousPlayerId = playerNow.getId();
        }
        System.out.println(BasicPhrases.REQUEST_LOGIN_AND_PASSWORD);
        String login = consoleReader.readStringInfo();
        String password = consoleReader.readStringInfo();
        playerNow = playerService.registration(login, password);
        if (previousPlayerId != null) {
            auditService.addAudit(AuditType.EXIT, previousPlayerId);
        }
        auditService.addAudit(AuditType.REGISTRATION, playerNow.getId());
    }

    /**
     * Метод для аутентификации игрока.
     *
     * @throws Exception ошибка при попытке аутентификации
     */
    private void authenticatePlayer() throws Exception {
        Long previousPlayerId = null;
        if (playerNow != null) {
            previousPlayerId = playerNow.getId();
        }
        System.out.println(BasicPhrases.REQUEST_LOGIN_AND_PASSWORD);
        String login = consoleReader.readStringInfo();
        String password = consoleReader.readStringInfo();
        playerNow = playerService.authorization(login, password);
        if (previousPlayerId != null) {
            auditService.addAudit(AuditType.EXIT, previousPlayerId);
        }
        auditService.addAudit(AuditType.AUTHORIZATION, playerNow.getId());
    }

    /**
     * Метод для печати баланса игрока.
     */
    private void printBalancePlayer() {
        System.out.println("Ваш баланс:" + playerNow.getBalance());
        auditService.addAudit(AuditType.BALANCE_REQUEST, playerNow.getId());
    }

    /**
     * Метод для снятия средств у игрока.
     *
     * @throws Exception ошибка при попытке снятия средств
     */
    private void debitForPlayer() throws Exception {
        System.out.println(BasicPhrases.AKS_FOR_TRANSACTION_ID);
        long transactionId = consoleReader.readTransactionId();
        System.out.println(BasicPhrases.ASK_FOR_DEBIT_SIZE);
        double debitSize = consoleReader.readDoubleNumber();
        playerNow = playerService.debitForPlayer(playerNow.getLogin(), transactionId, debitSize);
        System.out.println(BasicPhrases.SUCCESSFUL_OPERATION);
        auditService.addAudit(AuditType.DEBIT, playerNow.getId());
    }

    /**
     * Метод для пополнения средства.
     *
     * @throws Exception ошибка при попытке пополнения средств
     */
    private void creditForPlayer() throws Exception {
        System.out.println(BasicPhrases.AKS_FOR_TRANSACTION_ID);
        long transactionId = consoleReader.readTransactionId();
        System.out.println(BasicPhrases.ASK_FOR_CREDIT_SIZE);
        double creditSize = consoleReader.readDoubleNumber();
        playerNow = playerService.creditForPlayer(playerNow.getLogin(), transactionId, creditSize);
        System.out.println(BasicPhrases.SUCCESSFUL_OPERATION);
        auditService.addAudit(AuditType.CREDIT, playerNow.getId());
    }

    /**
     * Метод выводит в консоль историю пополнений.
     */
    private void printCreditHistory() {
        System.out.println(transactionService.getHistoryTransactions(playerNow.getId(), TransactionType.CREDIT));
        auditService.addAudit(AuditType.REQUEST_CREDIT_HISTORY, playerNow.getId());

    }

    /**
     * Метод выводит в консоль историю снятия.
     */
    private void printDebitHistory() {
        System.out.println(transactionService.getHistoryTransactions(playerNow.getId(), TransactionType.DEBIT));
        auditService.addAudit(AuditType.REQUEST_DEBIT_HISTORY, playerNow.getId());
    }

    /**
     * Метод выводит в консоль историю действий игрока.
     */
    private void printAudit() {
        System.out.println(auditService.findAuditsByLoginPlayer(playerNow.getId()));
    }
}
