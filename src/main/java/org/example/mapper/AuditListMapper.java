package org.example.mapper;

import org.example.domain.dto.AuditResponseDto;
import org.example.domain.model.Audit;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = AuditMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuditListMapper {

    List<AuditResponseDto> toResponsesAuditDto(List<Audit> audits);
}
