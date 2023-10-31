package org.example.mapper;

import org.example.dto.AuditResponseDto;
import org.example.model.Audit;
import org.mapstruct.Mapper;

@Mapper
public interface AuditMapper {
    AuditResponseDto toResponseDto(Audit audit);
}
