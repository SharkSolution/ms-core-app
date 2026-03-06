package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.WaiterDto;
import com.suresell.mscoreapp.domain.model.WaiterEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WaiterMapper {
    WaiterDto toDto(WaiterEntity entity);
    List<WaiterDto> toDtoList(List<WaiterEntity> entities);
}
