package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.ShoppingItem;
import com.suresell.mscoreapp.domain.model.ShoppingItemEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = false)
)
public interface ShoppingItemEntityMapper {

    @Mapping(target = "id", source = "id")

    @Mapping(target = "name", source = "name")
    @Mapping(target = "supplyCategory", source = "supplyCategory")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "currentStock", source = "currentStock")
    @Mapping(target = "minimumStock", source = "minimumStock")
    @Mapping(target = "suggestedQuantity", source = "suggestedQuantity")
    @Mapping(target = "estimatedCost", source = "estimatedCost")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ShoppingItem toDomain(ShoppingItemEntity entity);

    @Mapping(target = "id", source = "id")

    @Mapping(target = "supplyCategory", source = "supplyCategory")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "currentStock", source = "currentStock")
    @Mapping(target = "minimumStock", source = "minimumStock")
    @Mapping(target = "suggestedQuantity", source = "suggestedQuantity")
    @Mapping(target = "estimatedCost", source = "estimatedCost")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ShoppingItemEntity toEntity(ShoppingItem domain);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget ShoppingItemEntity entity, ShoppingItem domain);
}
