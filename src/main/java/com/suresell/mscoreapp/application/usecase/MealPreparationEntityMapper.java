package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.MealPreparation;
import com.suresell.mscoreapp.domain.model.MealPreparationEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MealPreparationEntityMapper {

    MealPreparation toDomain(MealPreparationEntity entity);

    MealPreparationEntity toEntity(MealPreparation domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget MealPreparationEntity entity, MealPreparation domain);
}
