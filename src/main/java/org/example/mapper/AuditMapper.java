package org.example.mapper;

import org.example.domain.dto.AuditResponseDto;
import org.example.domain.model.Audit;
import org.mapstruct.Mapper;

@Mapper
public interface AuditMapper {
    AuditResponseDto toResponseDto(Audit audit);
}
