package com.ms.catalog_service.mappers;

import com.ms.catalog_service.dtos.ShowResponseDto;
import com.ms.catalog_service.entities.Show;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShowMapper {
    ShowResponseDto toDto(Show entity);
    Show toEntity(ShowResponseDto dto);
}
