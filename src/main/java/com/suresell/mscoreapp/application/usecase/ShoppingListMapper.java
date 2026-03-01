package com.suresell.mscoreapp.application.usecase;


import com.suresell.mscoreapp.application.dto.ShoppingItem;
import com.suresell.mscoreapp.domain.model.ShoppingItemEntity;
import com.suresell.mscoreapp.application.dto.ShoppingListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShoppingListMapper {

    List<ShoppingItem> toDto(List<ShoppingItem> items);
    List<ShoppingItem> toEntity(List<ShoppingItemEntity> items);

    default ShoppingListResponse toResponse(List<ShoppingItem> items) {
        List<ShoppingItem> dtos = toDto(items);
        return new ShoppingListResponse(dtos);
    }
}
