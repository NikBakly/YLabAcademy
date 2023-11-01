package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.aop.annotations.LoggableService;
import org.example.domain.dto.PlayerRequestDto;
import org.example.domain.dto.PlayerResponseDto;
import org.example.domain.dto.TransactionRequestDto;
import org.example.domain.model.Player;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.exception.SaveEntityException;
import org.example.mapper.PlayerMapper;
import org.example.mapper.TransactionMapper;
import org.example.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@LoggableService
public class PlayerServiceImpl implements PlayerService {
    private static final Logger log = LogManager.getLogger(PlayerServiceImpl.class);

    private final PlayerRepository playerRepository;
    private final TransactionService transactionService;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, TransactionService transactionService) {
        this.playerRepository = playerRepository;
        this.transactionService = transactionService;
    }

    @Override
    public PlayerResponseDto registration(PlayerRequestDto playerRequestDto)
            throws InvalidInputException, SaveEntityException {
        checkLoginAndPasswordOrThrow(playerRequestDto);
        Player newPlayer = playerRepository.save(playerRequestDto.login(), playerRequestDto.password());
        log.debug("Игрок login={} успешно зарегистрирован.", playerRequestDto.login());
        return PlayerMapper.INSTANCE.toResponseDto(newPlayer);
    }

    @Override
    public PlayerResponseDto authorization(PlayerRequestDto playerRequestDto) throws InvalidInputException {
        checkLoginAndPasswordOrThrow(playerRequestDto);
        Optional<Player> foundPlayer = playerRepository.findByLogin(playerRequestDto.login());
        if (foundPlayer.isPresent() && foundPlayer.get().getPassword().equals(playerRequestDto.password())) {
            log.debug("Игрок login={} успешно авторизован.", playerRequestDto.login());
            return PlayerMapper.INSTANCE.toResponseDto(foundPlayer.get());
        } else {
            log.warn("Логин или пароль не валидны.");
            throw new InvalidInputException("Логин или пароль не валидны.");
        }
    }

    @Override
    public PlayerResponseDto debitForPlayer(String loginPlayer,
                                            TransactionRequestDto transactionRequestDto) throws RuntimeException {
        Player foundPlayer = findAndCheckPlayerByLogin(loginPlayer);
        BigDecimal balancePlayer = foundPlayer.getBalance();
        BigDecimal bigDecimalDebit = BigDecimal.valueOf(transactionRequestDto.size());
        int comparisonResult = balancePlayer.compareTo(bigDecimalDebit);
        //Когда баланс игрока < размер дебита
        if (comparisonResult < 0) {
            log.warn("У игрока login={} не достаточно средств для дебита.", foundPlayer.getLogin());
            throw new InvalidInputException("У вас нету столько средств на балансе.");
        }
        transactionService.createTransaction(TransactionMapper.INSTANCE
                .toEntity(transactionRequestDto, foundPlayer.getId()));
        foundPlayer.setBalance(balancePlayer.subtract(bigDecimalDebit));
        playerRepository.updateBalanceByLogin(loginPlayer, foundPlayer.getBalance().doubleValue());
        log.info("У игрока login={} успешно прошло действие дебит", loginPlayer);
        return PlayerMapper.INSTANCE.toResponseDto(foundPlayer);
    }

    @Override
    public PlayerResponseDto creditForPlayer(String loginPlayer,
                                             TransactionRequestDto transactionRequestDto) throws RuntimeException {
        Player foundPlayer = findAndCheckPlayerByLogin(loginPlayer);
        BigDecimal balancePlayer = foundPlayer.getBalance();
        transactionService.createTransaction(TransactionMapper.INSTANCE
                .toEntity(transactionRequestDto, foundPlayer.getId()));
        foundPlayer.setBalance(balancePlayer.add(BigDecimal.valueOf(transactionRequestDto.size())));
        playerRepository.updateBalanceByLogin(loginPlayer, foundPlayer.getBalance().doubleValue());
        log.info("У игрока login={} успешно прошло действие кредит", loginPlayer);
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
