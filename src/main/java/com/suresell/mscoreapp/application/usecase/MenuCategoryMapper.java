package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.MenuCategoryDto;
import com.suresell.mscoreapp.domain.model.MenuCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuCategoryMapper {

    MenuCategoryDto toDto(MenuCategoryEntity entity);

    List<MenuCategoryDto> toDtoList(List<MenuCategoryEntity> entities);

    MenuCategoryEntity toEntity(MenuCategoryDto dto);

    void updateEntity(@MappingTarget MenuCategoryEntity entity, MenuCategoryDto dto);
}
