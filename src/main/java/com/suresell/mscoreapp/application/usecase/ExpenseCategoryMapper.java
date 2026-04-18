package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.CreateExpenseCategoryRequest;
import com.suresell.mscoreapp.application.dto.ExpenseCategoryDto;
import com.suresell.mscoreapp.domain.model.ExpenseCategoryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ExpenseCategoryMapper {

    ExpenseCategoryDto toDto(ExpenseCategoryEntity entity);

    List<ExpenseCategoryDto> toDtoList(List<ExpenseCategoryEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExpenseCategoryEntity toEntity(CreateExpenseCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget ExpenseCategoryEntity entity, CreateExpenseCategoryRequest request);
}
