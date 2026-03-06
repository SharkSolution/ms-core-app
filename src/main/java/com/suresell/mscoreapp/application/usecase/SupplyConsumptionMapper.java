package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.SupplyConsumptionDto;
import com.suresell.mscoreapp.domain.model.SupplyConsumptionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplyConsumptionMapper {

    SupplyConsumptionDto toDto(SupplyConsumptionEntity entity);

    SupplyConsumptionEntity toEntity(SupplyConsumptionDto dto);

    List<SupplyConsumptionDto> toDtoList(List<SupplyConsumptionEntity> entityList);
}
