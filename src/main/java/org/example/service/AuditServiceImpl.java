package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dto.AuditResponseDto;
import org.example.mapper.AuditListMapper;
import org.example.model.Audit;
import org.example.model.Player;
import org.example.repository.AuditRepository;
import org.example.repository.AuditRepositoryImpl;
import org.example.repository.PlayerRepository;
import org.example.repository.PlayerRepositoryImpl;
import org.example.util.AuditType;

import java.util.List;
import java.util.Optional;

/**
 * Класс реализующий бизнес-логику для сущности Audit.
 */
public class AuditServiceImpl implements AuditService {
    private static final Logger log = LogManager.getLogger(AuditServiceImpl.class);
    private static AuditServiceImpl instance;

    private final AuditRepository auditRepository;
    private final PlayerRepository playerRepository;

    private AuditServiceImpl() {
        this.auditRepository = new AuditRepositoryImpl();
        this.playerRepository = new PlayerRepositoryImpl();
    }

    public AuditServiceImpl(AuditRepository auditRepository, PlayerRepository playerRepository) {
        this.auditRepository = auditRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * Метод для реализации шаблона проектирования Singleton.
     *
     * @return сущность AuditService
     */
    public static AuditServiceImpl getInstance() {
        if (instance == null) {
            instance = new AuditServiceImpl();
        }
        return instance;
    }

    @Override
    public void createAudit(AuditType auditType, Long playerId) {
        auditRepository.createAudit(auditType, playerId);
        log.info("Аудит успешно добавился к игроку с id={}", playerId);
    }

    @Override
    public void createAudit(AuditType auditType, String loginPlayer) {
        Optional<Player> foundPlayer = playerRepository.findByLogin(loginPlayer);
        if (foundPlayer.isPresent()) {
            Long playerId = foundPlayer.get().getId();
            createAudit(auditType, playerId);
        } else {
            log.warn("Аудит не добавился, т к игрок не найден");
        }
    }

    @Override
    public List<AuditResponseDto> findAuditsByLoginPlayer(Long playerId) {
        List<Audit> foundAudits = auditRepository.findAuditsByPlayerIdByCreatedTime(playerId);
        log.info("Все аудита игрока с id={} найдены", playerId);
        return AuditListMapper.INSTANCE.toResponsesAuditDto(foundAudits);
    }
}