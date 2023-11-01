package org.example.mapper;

import org.example.domain.dto.AuditResponseDto;
import org.example.domain.model.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuditMapper {
    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    AuditResponseDto toResponseDto(Audit audit);
}
