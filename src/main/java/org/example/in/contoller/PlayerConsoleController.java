package org.example.in.contoller;

import org.example.exception.InvalidInputException;
import org.example.exception.SaveEntityException;
import org.example.in.ConsoleReader;
import org.example.model.Player;
import org.example.out.ConsolePrinter;
import org.example.service.AuditService;
import org.example.service.PlayerService;
import org.example.service.TransactionService;
import org.example.util.AuditType;
import org.example.util.BasicPhrases;

/**
 * Класс, который ответственный за обработку поступающих запросов
 */
public class PlayerConsoleController {
    private static PlayerConsoleController instance;

    private static final int EXIT_CODE = 0;
    private static final int REGISTRATION_CODE = 1;
    private static final int AUTHORIZATION_CODE = 2;
    private static final int BALANCE_CODE = 3;
    private static final int DEBIT_CODE = 4;
    private static final int CREDIT_CODE = 5;
    private static final int DEBIT_HISTORY_CODE = 6;
    private static final int CREDIT_HISTORY_CODE = 7;
    private static final int AUDIT_CODE = 8;

    //TODO передалать инициализацию, сделать DI
    private final PlayerService playerService = PlayerService.getInstance();
    private final AuditService auditService = AuditService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();
    private final ConsoleReader consoleReader = ConsoleReader.getInstance();
    private final ConsolePrinter consolePrinter = ConsolePrinter.getInstance();

    /**
     * Поле для отслеживания окончания обработки пользовательских запросов
     */
    private boolean isFinish = false;
    /**
     * Поле для отслеживания авторизованного игрока
     */
    private Player playerNow = null;

    private PlayerConsoleController() {
    }

    /**
     * Метод для реализации шаблона проектирования Singleton
     *
     * @return сущность PlayerConsoleController
     */
    public static PlayerConsoleController getInstance() {
        if (instance == null) {
            instance = new PlayerConsoleController();
        }
        return instance;
    }

    /**
     * Метод запускающий цикл для обработки запросов пользователя
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
                consolePrinter.printMessage(e.getMessage());
            } catch (Exception e) {
                consolePrinter.printMessage(BasicPhrases.ERROR_READING_FROM_CONSOLE);
            }
        }
        consolePrinter.printMessage(BasicPhrases.GOODBYE);
        consoleReader.close();
    }

    /**
     * Метод для обработки не авторизованного игрока
     *
     * @throws Exception отслеживания ошибка при обработке
     */
    private void processUnauthorizedPlayer() throws Exception {
        consolePrinter.printMessage(BasicPhrases.FOR_UNAUTHORIZED);
        int typeOperation = consoleReader.readTypeOperation();
        switch (typeOperation) {
            case EXIT_CODE -> isFinish = true;
            case REGISTRATION_CODE, AUTHORIZATION_CODE -> registerOrAuthorizePlayer(typeOperation);
            default -> consolePrinter.printMessage(BasicPhrases.COMMAND_NOT_FOUND);
        }
    }

    /**
     * Метод для обработки авторизованного игрока
     *
     * @throws Exception отслеживания ошибка при обработке
     */
    private void processAuthorizedPlayer() throws Exception {
        consolePrinter.printMessage(BasicPhrases.FOR_AUTHORIZED);
        int typeOperation = consoleReader.readTypeOperation();
        switch (typeOperation) {
            case EXIT_CODE -> isFinish = true;
            case REGISTRATION_CODE, AUTHORIZATION_CODE -> registerOrAuthorizePlayer(typeOperation);
            case BALANCE_CODE -> printBalancePlayer();
            case DEBIT_CODE -> debitForPlayer();
            case CREDIT_CODE -> creditForPlayer();
            case CREDIT_HISTORY_CODE -> printCreditHistory();
            case AUDIT_CODE -> printAudit();
            case DEBIT_HISTORY_CODE -> printDebitHistory();
            default -> {
                auditService.addAudit(AuditType.ERROR_ENTERING_COMMAND, playerNow.getLogin());
                consolePrinter.printMessage(BasicPhrases.COMMAND_NOT_FOUND);
            }

        }
    }

    /**
     * Метод для авторизации или регистрации игрока
     *
     * @param typeOperation тип операции пользователя
     * @return авторизированный или зарегистрированный игрок
     * @throws Exception ошибка при попытке авторизации
     */
    private void registerOrAuthorizePlayer(int typeOperation) throws Exception {
        String previousLoginPlayer = null;
        if (playerNow != null) {
            previousLoginPlayer = playerNow.getLogin();
        }
        consolePrinter.printMessage(BasicPhrases.REQUEST_LOGIN_AND_PASSWORD);
        String login = consoleReader.readStringInfo();
        String password = consoleReader.readStringInfo();
        playerNow = (typeOperation == REGISTRATION_CODE) ?
                playerService.registration(login, password) :
                playerService.authorization(login, password);
        if (previousLoginPlayer != null) {
            auditService.addAudit(AuditType.EXIT, previousLoginPlayer);
        }
        if (typeOperation == REGISTRATION_CODE) {
            auditService.addAudit(AuditType.REGISTRATION, playerNow.getLogin());
        } else {
            auditService.addAudit(AuditType.AUTHORIZATION, playerNow.getLogin());
        }
    }

    /**
     * Метод для печати баланса игрока
     */
    private void printBalancePlayer() {
        consolePrinter.printMessage("Ваш баланс:" + playerNow.getBalance());
        auditService.addAudit(AuditType.BALANCE_REQUEST, playerNow.getLogin());
    }

    /**
     * Метод для снятия средств у игрока
     *
     * @throws Exception ошибка при попытке снятия средств
     */
    private void debitForPlayer() throws Exception {
        consolePrinter.printMessage(BasicPhrases.AKS_FOR_TRANSACTION_ID);
        long transactionId = consoleReader.readTransactionId();
        consolePrinter.printMessage(BasicPhrases.ASK_FOR_DEBIT_SIZE);
        double debitSize = consoleReader.readDoubleNumber();
        playerService.debitForPlayer(playerNow, transactionId, debitSize);
        consolePrinter.printMessage(BasicPhrases.SUCCESSFUL_OPERATION);
        auditService.addAudit(AuditType.DEBIT, playerNow.getLogin());
    }

    /**
     * Метод для пополнения средства
     *
     * @throws Exception ошибка при попытке пополнения средств
     */
    private void creditForPlayer() throws Exception {
        consolePrinter.printMessage(BasicPhrases.AKS_FOR_TRANSACTION_ID);
        long transactionId = consoleReader.readTransactionId();
        consolePrinter.printMessage(BasicPhrases.ASK_FOR_DEBIT_SIZE);
        double creditSize = consoleReader.readDoubleNumber();
        playerService.creditForPlayer(playerNow, transactionId, creditSize);
        consolePrinter.printMessage(BasicPhrases.SUCCESSFUL_OPERATION);
        auditService.addAudit(AuditType.CREDIT, playerNow.getLogin());
    }

    private void printCreditHistory() {
        System.out.println(transactionService.getCreditHistoryTransactions(playerNow.getLogin()));
        auditService.addAudit(AuditType.REQUEST_CREDIT_HISTORY, playerNow.getLogin());

    }

    private void printDebitHistory() {
        System.out.println(transactionService.getDebitHistoryTransactions(playerNow.getLogin()));
        auditService.addAudit(AuditType.REQUEST_DEBIT_HISTORY, playerNow.getLogin());
    }

    private void printAudit() {
        System.out.println(auditService.findAuditsByLoginPlayer(playerNow.getLogin()));
    }
}
