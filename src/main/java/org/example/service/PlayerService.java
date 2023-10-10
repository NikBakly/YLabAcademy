package org.example.service;

import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.exception.SaveEntityException;
import org.example.model.Player;
import org.example.repository.PlayerInMemoryRepository;
import org.example.util.TransactionType;

/**
 * Класс ответственный за бизнес-логику для сущности Player.
 */
public class PlayerService {
    private static PlayerService instance;

    private final PlayerInMemoryRepository playerInMemoryRepository;
    private final TransactionService transactionService;


    private PlayerService() {
        this.playerInMemoryRepository = PlayerInMemoryRepository.getInstance();
        this.transactionService = TransactionService.getInstance();
    }

    public PlayerService(PlayerInMemoryRepository playerInMemoryRepository, TransactionService transactionService) {
        this.playerInMemoryRepository = playerInMemoryRepository;
        this.transactionService = transactionService;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton.
     *
     * @return сущность PlayerService
     */
    public static PlayerService getInstance() {
        if (instance == null) {
            instance = new PlayerService();
        }

        return instance;
    }

    /**
     * Метод для регистрации игрока.
     *
     * @param login    логин игрока.
     * @param password пароль игрока.
     * @return зарегистрированный игрок.
     * @throws InvalidInputException если переданные данные пустые.
     * @throws SaveEntityException   ошибка при создании сущности.
     */
    public Player registration(String login, String password) throws InvalidInputException, SaveEntityException {
        checkLoginAndPasswordOrThrow(login, password);
        Player newPlayer = playerInMemoryRepository.save(login, password);
        System.out.println("Игрок " + login + " успешно зарегистрирован!\n");
        return newPlayer;
    }

    /**
     * Метод для авторизации игрока.
     *
     * @param login    логин игрока.
     * @param password пароль игрока.
     * @return зарегистрированный игрок.
     * @throws InvalidInputException если переданные данные пустые.
     */
    public Player authorization(String login, String password) throws InvalidInputException {
        checkLoginAndPasswordOrThrow(login, password);
        Player foundPlayer = playerInMemoryRepository.findByLogin(login);
        if (foundPlayer != null && foundPlayer.getPassword().equals(password)) {
            System.out.println("Вы успешно авторизованы!\n");
            return foundPlayer;
        } else {
            System.out.println("Вы ошиблись при вводе логина или пароля!\n");
            return null;
        }
    }

    /**
     * Метод для выполнения дебит(списание средств) операции для игрока.
     *
     * @param player        игрок, к которому будет выполнена операция дебит.
     * @param transactionId уникальный id транзакции.
     * @param debitSize     размер средств для списания.
     * @throws RuntimeException ошибка при не выполненной операции.
     */
    public void debitForPlayer(Player player, long transactionId, double debitSize) throws RuntimeException {
        checkPlayer(player);
        double balancePlayer = player.getBalance();
        if (balancePlayer < debitSize) {
            throw new InvalidInputException("У вас нету столько средств на балансе.");
        }
        transactionService.createTransaction(transactionId, TransactionType.DEBIT, debitSize, player.getLogin());
        player.setBalance(balancePlayer - debitSize);
    }

    /**
     * Метод для выполнения кредит(пополнения средств) операции для игрока.
     *
     * @param player        игрок, к которому будет выполнена операция дебит.
     * @param transactionId уникальный id транзакции.
     * @param creditSize    размер средств для пополнения.
     * @throws RuntimeException ошибка при не выполненной операции.
     */
    public void creditForPlayer(Player player, long transactionId, double creditSize) throws RuntimeException {
        checkPlayer(player);
        double balancePlayer = player.getBalance();
        if (Double.isInfinite(balancePlayer + creditSize)) {
            throw new InvalidInputException("У вас будет слишком большой баланс. " +
                    "Обратитесь в тех. поддержку для решения данного вопроса!");
        }
        transactionService.createTransaction(transactionId, TransactionType.CREDIT, creditSize, player.getLogin());
        player.setBalance(balancePlayer + creditSize);
    }

    /**
     * Метод для проверки игрока на существование и на пустоту.
     *
     * @param player передаваемый объект для проверки
     * @throws NotFoundException ошибка, если объект игрока не прошел проверку
     */
    private void checkPlayer(Player player) throws NotFoundException {
        if (player == null || playerInMemoryRepository.findByLogin(player.getLogin()) == null) {
            throw new NotFoundException("Игрок не найден или передана пустота");
        }
    }

    /**
     * Метод для проверки логина и пароля на пустоту.
     *
     * @param login    логин для проверки.
     * @param password пароль для проверки.
     * @throws InvalidInputException если переданные данные пустые.
     */
    private void checkLoginAndPasswordOrThrow(String login, String password) throws InvalidInputException {
        if (login == null || password == null || login.isBlank() || password.isBlank()) {
            throw new InvalidInputException("Логин или пароль не может быть пустым.");
        }
    }

}
