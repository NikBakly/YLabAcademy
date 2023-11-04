package org.example.aop.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.example.domain.dto.PlayerRequestDto;
import org.example.domain.model.Transaction;
import org.example.service.AuditService;
import org.example.util.AuditType;
import org.example.util.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class LoggableServiceAspectTest {
    @Mock
    private AuditService auditService;

    @InjectMocks
    private LoggableServiceAspect loggableServiceAspect;

    @BeforeAll
    static void init() {
        //Отключение аспектов
        System.setProperty("disableAspect", "false");
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loggableServiceAspect = new LoggableServiceAspect(auditService);
    }

    @Test
    @DisplayName("Удачное логирование метода")
    public void testLogging() throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn("testMethod");
        loggableServiceAspect.logging(proceedingJoinPoint);

        verify(proceedingJoinPoint).proceed();
    }

    @Test
    @DisplayName("Добавление аудита, когда игрок регистрируется")
    public void testAddAuditWhenRegistrationPlayer() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("registration");
        PlayerRequestDto requestDto = mock(PlayerRequestDto.class);
        when(requestDto.login()).thenReturn("tester");
        when(joinPoint.getArgs()).thenReturn(new Object[]{requestDto});

        loggableServiceAspect.addAuditFromPlayerServiceImpl(joinPoint);

        verify(auditService).createAudit(eq(AuditType.REGISTRATION), anyString());
    }

    @Test
    @DisplayName("Добавление аудита, когда игрок авторизуется")
    public void testAddAuditWhenAuthorizationPlayer() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("authorization");
        PlayerRequestDto requestDto = mock(PlayerRequestDto.class);
        when(requestDto.login()).thenReturn("tester");
        when(joinPoint.getArgs()).thenReturn(new Object[]{requestDto});

        loggableServiceAspect.addAuditFromPlayerServiceImpl(joinPoint);

        verify(auditService).createAudit(eq(AuditType.AUTHORIZATION), anyString());
    }


    @Test
    @DisplayName("Добавление аудита, когда игрок создать транзакцию типа кредит")
    public void testAddAuditFromTransactionServiceImplWhenCreateCreditTransaction() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("createTransaction");
        Transaction transactionMock = mock(Transaction.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{transactionMock});
        when(transactionMock.type()).thenReturn(TransactionType.CREDIT);

        loggableServiceAspect.addAuditFromTransactionServiceImpl(joinPoint);

        verify(auditService).createAudit(eq(AuditType.CREDIT), anyLong());
    }

    @Test
    @DisplayName("Добавление аудита, когда игрок создать транзакцию типа дебит")
    public void testAddAuditFromTransactionServiceImplWhenCreateDebitTransaction() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("createTransaction");
        Transaction transactionMock = mock(Transaction.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{transactionMock});
        when(transactionMock.type()).thenReturn(TransactionType.DEBIT);

        loggableServiceAspect.addAuditFromTransactionServiceImpl(joinPoint);

        verify(auditService).createAudit(eq(AuditType.DEBIT), anyLong());
    }

    @Test
    @DisplayName("Добавление аудита, когда игрок запросил историю транзакций типа кредит")
    public void testAddAuditFromTransactionServiceImplWhenFindHistoryCreditTransactions() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("findHistoryTransactions");

        long playerId = 1L;
        TransactionType transactionType = TransactionType.CREDIT;
        when(joinPoint.getArgs()).thenReturn(new Object[]{playerId, transactionType});

        loggableServiceAspect.addAuditFromTransactionServiceImpl(joinPoint);

        verify(auditService).createAudit(eq(AuditType.REQUEST_CREDIT_HISTORY), anyLong());
    }

    @Test
    @DisplayName("Добавление аудита, когда игрок запросил историю транзакций типа дебит")
    public void testAddAuditFromTransactionServiceImplWhenFindHistoryDebitTransactions() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("findHistoryTransactions");

        long playerId = 1L;
        TransactionType transactionType = TransactionType.DEBIT;
        when(joinPoint.getArgs()).thenReturn(new Object[]{playerId, transactionType});

        loggableServiceAspect.addAuditFromTransactionServiceImpl(joinPoint);

        verify(auditService).createAudit(eq(AuditType.REQUEST_DEBIT_HISTORY), anyLong());
    }
}