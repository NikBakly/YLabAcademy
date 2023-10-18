package org.example.service;

import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.exception.SaveEntityException;
import org.example.model.Player;
import org.example.repository.PlayerRepository;
import org.example.repository.PlayerRepositoryImpl;
import org.example.util.TransactionType;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Класс реализующий бизнес-логику для сущности Player.
 */
public class PlayerServiceImpl implements PlayerService {
    private static PlayerServiceImpl instance;

    private final PlayerRepository playerRepository;
    private final TransactionService transactionService;


    private PlayerServiceImpl() {
        this.playerRepository = new PlayerRepositoryImpl();
        this.transactionService = TransactionServiceImpl.getInstance();
    }

    public PlayerServiceImpl(PlayerRepository playerRepository, TransactionServiceImpl transactionService) {
        this.playerRepository = playerRepository;
        this.transactionService = transactionService;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton.
     *
     * @return сущность PlayerService
     */
    public static PlayerServiceImpl getInstance() {
        if (instance == null) {
            instance = new PlayerServiceImpl();
        }
        return instance;
    }

    @Override
    public Player registration(String login, String password) throws InvalidInputException, SaveEntityException {
        checkLoginAndPasswordOrThrow(login, password);
        Player newPlayer = playerRepository.save(login, password);
        System.out.println("Игрок " + login + " успешно зарегистрирован!\n");
        return newPlayer;
    }

    @Override
    public Player authorization(String login, String password) throws InvalidInputException {
        checkLoginAndPasswordOrThrow(login, password);
        Optional<Player> foundPlayer = playerRepository.findByLogin(login);
        if (foundPlayer.isPresent() && foundPlayer.get().getPassword().equals(password)) {
            System.out.println("Вы успешно авторизованы!\n");
            return foundPlayer.get();
        } else {
            System.out.println("Вы ошиблись при вводе логина или пароля!\n");
            return null;
        }
    }

    @Override
    public Player debitForPlayer(String loginPlayer, long transactionId, double debitSize) throws RuntimeException {
        Player foundPlayer = findAndCheckPlayerByLogin(loginPlayer);
        BigDecimal balancePlayer = foundPlayer.getBalance();
        BigDecimal bigDecimalDebit = BigDecimal.valueOf(debitSize);
        // Сравнение двух BigDecimal чисел
        int comparisonResult = balancePlayer.compareTo(bigDecimalDebit);
        //Когда balancePlayer < bigDecimalDebit
        if (comparisonResult < 0) {
            throw new InvalidInputException("У вас нету столько средств на балансе.");
        }
        transactionService.createTransaction(transactionId, TransactionType.DEBIT, debitSize, foundPlayer.getLogin());
        foundPlayer.setBalance(balancePlayer.subtract(bigDecimalDebit));
        playerRepository.updateBalanceByLogin(loginPlayer, foundPlayer.getBalance().doubleValue());
        return foundPlayer;
    }

    @Override
    public Player creditForPlayer(String loginPlayer, long transactionId, double creditSize) throws RuntimeException {
        Player foundPlayer = findAndCheckPlayerByLogin(loginPlayer);
        BigDecimal balancePlayer = foundPlayer.getBalance();
        transactionService.createTransaction(transactionId, TransactionType.CREDIT, creditSize, foundPlayer.getLogin());
        foundPlayer.setBalance(balancePlayer.add(BigDecimal.valueOf(creditSize)));
        playerRepository.updateBalanceByLogin(loginPlayer, foundPlayer.getBalance().doubleValue());
        return foundPlayer;
    }

    private Player findAndCheckPlayerByLogin(String loginPlayer) throws NotFoundException {
        Optional<Player> foundPlayer = playerRepository.findByLogin(loginPlayer);
        if (foundPlayer.isEmpty()) {
            throw new NotFoundException("Игрок не найден.");
        }
        return foundPlayer.get();
    }

    private void checkLoginAndPasswordOrThrow(String login, String password) throws InvalidInputException {
        if (login == null || password == null || login.isBlank() || password.isBlank()) {
            throw new InvalidInputException("Логин или пароль не может быть пустым.");
        }
    }
}
