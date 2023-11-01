package org.example.aop.aspects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.domain.dto.PlayerRequestDto;
import org.example.domain.model.Transaction;
import org.example.service.AuditService;
import org.example.util.AuditType;
import org.example.util.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Основной аспект для логирования сервисный действий
 */
@Aspect
@Component
public class LoggableServiceAspect {
    private static final Logger log = LogManager.getLogger(LoggableServiceAspect.class);

    private final AuditService auditService;

    @Autowired
    public LoggableServiceAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Метод для определения методов из классов, помеченные аннотацией LoggableService, к которым будет применяться аспект.
     */
    @Pointcut("within(@org.example.aop.annotations.LoggableService *) && execution(* *(..))")
    public void annotatedByLoggableService() {
    }

    /**
     * Метод для определения методов из PlayerServiceImpl, к которым будет применяться аспект.
     */
    @Pointcut("within(org.example.service.PlayerServiceImpl) && execution(public* *(..))")
    public void annotatedByPlayerServiceImpl() {
    }

    /**
     * Метод для определения методов из TransactionServiceImpl, к которым будет применяться аспект.
     */
    @Pointcut("within(org.example.service.TransactionServiceImpl) && execution(public* *(..))")
    public void annotatedByTransactionServiceImpl() {
    }

    /**
     * Метод для замера времени выполнения методов из классов, помеченными аннотацией LoggableService
     *
     * @param proceedingJoinPoint интерфейс, который обеспечивает доступ к текущему состоянию процесса
     * @return результат рассмотрения
     * @throws Throwable если вызванный процесс выдает исключение
     */
    @Around("annotatedByLoggableService()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Calling method {}", proceedingJoinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("Execution of method {} finished. Execution time is {} ms.",
                proceedingJoinPoint.getSignature(),
                (endTime - startTime));
        return result;
    }

    /**
     * Метод выполняется после успешного завершения функций из класса PlayerServiceImpl
     *
     * @param joinPoint интерфейс, который обеспечивает доступ к выполненному состоянию процесса
     */
    @After("annotatedByPlayerServiceImpl()")
    public void addAuditFromPlayerServiceImpl(JoinPoint joinPoint) {
        String nameMethod = joinPoint.getSignature().getName();

        AuditType auditType = getAuditTypeByNameMethod(nameMethod);
        String loginPlayer = getPlayerLogin(auditType, joinPoint.getArgs());

        if (loginPlayer != null) {
            auditService.createAudit(auditType, loginPlayer);
        }
    }

    /**
     * Метод выполняется после успешного завершения функций из класса TransactionServiceImpl
     *
     * @param joinPoint интерфейс, который обеспечивает доступ к выполненному состоянию процесса
     */
    @After("annotatedByTransactionServiceImpl()")
    public void addAuditFromTransactionServiceImpl(JoinPoint joinPoint) {
        String nameMethod = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        if (nameMethod.equals("createTransaction")) {
            Transaction transaction = (Transaction) args[0];
            AuditType auditType = getAuditTypeByTransactionType(transaction.type());
            Long playerId = transaction.playerId();
            auditService.createAudit(auditType, playerId);
        } else if (nameMethod.equals("findHistoryTransactions")) {
            Long playerId = (Long) args[0];
            TransactionType transactionType = (TransactionType) args[1];
            AuditType auditType = getAuditTypeForHistoryByTransactionType(transactionType);
            auditService.createAudit(auditType, playerId);
        } else {
            log.warn("Не известный метод в классе TransactionServiceImpl");
        }
    }

    /**
     * Метод возвращает тип аудита
     *
     * @param nameMethod имя метода, который выполнился
     * @return тип аудита
     */
    private AuditType getAuditTypeByNameMethod(String nameMethod) {
        AuditType result = null;
        switch (nameMethod) {
            case "registration" -> result = AuditType.REGISTRATION;
            case "authorization" -> result = AuditType.AUTHORIZATION;
        }
        if (result == null) {
            log.warn("Встретился не известный метод в PlayerServiceImpl");
        }
        return result;
    }

    /**
     * Метод возвращает тип аудита по типу транзакции
     *
     * @param type тип транзакции
     * @return тип аудита
     */
    private AuditType getAuditTypeByTransactionType(TransactionType type) {
        AuditType result = null;
        switch (type) {
            case CREDIT -> result = AuditType.CREDIT;
            case DEBIT -> result = AuditType.DEBIT;
        }
        if (result == null) {
            log.warn("Встретился не известный тип транзакции");
        }
        return result;
    }

    /**
     * Метод возвращает тип аудита для истории транзакций по типу транзакции
     *
     * @param type тип транзакции
     * @return тип аудита
     */
    private AuditType getAuditTypeForHistoryByTransactionType(TransactionType type) {
        AuditType result = null;
        switch (type) {
            case CREDIT -> result = AuditType.REQUEST_CREDIT_HISTORY;
            case DEBIT -> result = AuditType.REQUEST_DEBIT_HISTORY;
        }
        if (result == null) {
            log.warn("Встретился не известный тип транзакции");
        }
        return result;
    }

    /**
     * Метод возвращает логин игрока
     *
     * @param auditType тип аудита
     * @param args      аргументы из функции, которая обрабатывается аспектом
     * @return логин игрока
     */
    private String getPlayerLogin(AuditType auditType, Object[] args) {
        if (auditType == null) {
            return null;
        }
        String result = null;
        if (auditType.equals(AuditType.REGISTRATION) ||
                auditType.equals(AuditType.AUTHORIZATION)) {
            PlayerRequestDto playerRequestDto = (PlayerRequestDto) args[0];
            result = playerRequestDto.login();
        }
        return result;
    }
}
