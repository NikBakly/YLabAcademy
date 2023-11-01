package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.dto.AuditResponseDto;
import org.example.domain.model.Audit;
import org.example.domain.model.Player;
import org.example.mapper.AuditListMapper;
import org.example.repository.AuditRepository;
import org.example.repository.PlayerRepository;
import org.example.util.AuditType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Класс реализующий бизнес-логику для сущности Audit.
 */
@Service
public class AuditServiceImpl implements AuditService {
    private static final Logger log = LogManager.getLogger(AuditServiceImpl.class);

    private final AuditRepository auditRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public AuditServiceImpl(AuditRepository auditRepository, PlayerRepository playerRepository) {
        this.auditRepository = auditRepository;
        this.playerRepository = playerRepository;
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