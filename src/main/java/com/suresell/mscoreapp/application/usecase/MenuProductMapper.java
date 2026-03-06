package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.MenuProductDto;
import com.suresell.mscoreapp.domain.model.MenuProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    MenuProductDto toDto(MenuProductEntity entity);

    List<MenuProductDto> toDtoList(List<MenuProductEntity> entities);

    @Mapping(target = "category.id", source = "categoryId")
    MenuProductEntity toEntity(MenuProductDto dto);

    @Mapping(target = "category.id", source = "categoryId")
    void updateEntity(@MappingTarget MenuProductEntity entity, MenuProductDto dto);
}
