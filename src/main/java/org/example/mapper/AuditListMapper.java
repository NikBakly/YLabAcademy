package org.example.mapper;

import org.example.domain.dto.AuditResponseDto;
import org.example.domain.model.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = AuditMapper.class)
public interface AuditListMapper {
    AuditListMapper INSTANCE = Mappers.getMapper(AuditListMapper.class);

    List<AuditResponseDto> toResponsesAuditDto(List<Audit> audits);
}
