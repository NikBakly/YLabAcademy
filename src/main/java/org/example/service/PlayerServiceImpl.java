package org.example.service;

import org.example.aop.annotations.Loggable;
import org.example.dto.PlayerRequestDto;
import org.example.dto.PlayerResponseDto;
import org.example.dto.TransactionRequestDto;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.exception.SaveEntityException;
import org.example.mapper.PlayerMapper;
import org.example.mapper.TransactionMapper;
import org.example.model.Player;
import org.example.repository.PlayerRepository;
import org.example.repository.PlayerRepositoryImpl;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Класс реализующий бизнес-логику для сущности Player.
 */
@Loggable
public class PlayerServiceImpl implements PlayerService {
    private static PlayerServiceImpl instance;

    private final PlayerRepository playerRepository;
    private final TransactionService transactionService;


    private PlayerServiceImpl() {
        this.playerRepository = new PlayerRepositoryImpl();
        this.transactionService = TransactionServiceImpl.getInstance();
    }

    public PlayerServiceImpl(PlayerRepository playerRepository, TransactionService transactionService) {
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
    public PlayerResponseDto registration(PlayerRequestDto playerRequestDto) throws InvalidInputException, SaveEntityException {
        checkLoginAndPasswordOrThrow(playerRequestDto);
        Player newPlayer = playerRepository.save(playerRequestDto.login(), playerRequestDto.password());
        System.out.println("Игрок " + playerRequestDto.login() + " успешно зарегистрирован!\n");
        return PlayerMapper.INSTANCE.toResponseDto(newPlayer);
    }

    @Override
    public PlayerResponseDto authorization(PlayerRequestDto playerRequestDto) throws InvalidInputException {
        checkLoginAndPasswordOrThrow(playerRequestDto);
        Optional<Player> foundPlayer = playerRepository.findByLogin(playerRequestDto.login());
        if (foundPlayer.isPresent() && foundPlayer.get().getPassword().equals(playerRequestDto.password())) {
            System.out.println("Вы успешно авторизованы!\n");
            return PlayerMapper.INSTANCE.toResponseDto(foundPlayer.get());
        } else {
            System.out.println("Вы ошиблись при вводе логина или пароля!\n");
            throw new InvalidInputException("Вы ошиблись при вводе логина или пароля!");
        }
    }

    @Override
    public PlayerResponseDto debitForPlayer(String loginPlayer, TransactionRequestDto transactionRequestDto) throws RuntimeException {
        Player foundPlayer = findAndCheckPlayerByLogin(loginPlayer);
        BigDecimal balancePlayer = foundPlayer.getBalance();
        BigDecimal bigDecimalDebit = BigDecimal.valueOf(transactionRequestDto.size());
        int comparisonResult = balancePlayer.compareTo(bigDecimalDebit);
        //Когда balancePlayer < bigDecimalDebit
        if (comparisonResult < 0) {
            throw new InvalidInputException("У вас нету столько средств на балансе.");
        }
        transactionService.createTransaction(TransactionMapper.INSTANCE.toEntity(transactionRequestDto));
        foundPlayer.setBalance(balancePlayer.subtract(bigDecimalDebit));
        playerRepository.updateBalanceByLogin(loginPlayer, foundPlayer.getBalance().doubleValue());
        return PlayerMapper.INSTANCE.toResponseDto(foundPlayer);
    }

    @Override
    public PlayerResponseDto creditForPlayer(String loginPlayer, TransactionRequestDto transactionRequestDto) throws RuntimeException {
        Player foundPlayer = findAndCheckPlayerByLogin(loginPlayer);
        BigDecimal balancePlayer = foundPlayer.getBalance();
        transactionService.createTransaction(TransactionMapper.INSTANCE.toEntity(transactionRequestDto));
        foundPlayer.setBalance(balancePlayer.add(BigDecimal.valueOf(transactionRequestDto.size())));
        playerRepository.updateBalanceByLogin(loginPlayer, foundPlayer.getBalance().doubleValue());
        return PlayerMapper.INSTANCE.toResponseDto(foundPlayer);
    }

    /**
     * Метод для проверки игрока в БД
     *
     * @param loginPlayer логин игрока
     * @return найденный игрока
     * @throws NotFoundException если игрока не найден
     */
    private Player findAndCheckPlayerByLogin(String loginPlayer) throws NotFoundException {
        Optional<Player> foundPlayer = playerRepository.findByLogin(loginPlayer);
        if (foundPlayer.isEmpty()) {
            throw new NotFoundException("Игрок не найден.");
        }
        return foundPlayer.get();
    }

    /**
     * Метод для проверки валидности логин и пароля игрока
     *
     * @param playerRequestDto дто объект игрока
     * @throws InvalidInputException если проверка не пройдена
     */
    private void checkLoginAndPasswordOrThrow(PlayerRequestDto playerRequestDto) throws InvalidInputException {
        if (playerRequestDto.login() == null || playerRequestDto.password() == null ||
                playerRequestDto.login().isBlank() || playerRequestDto.password().isBlank()) {
            throw new InvalidInputException("Логин или пароль не может быть пустым.");
        }
    }
}
