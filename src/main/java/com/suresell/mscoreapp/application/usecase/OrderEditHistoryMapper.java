package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.OrderEditHistoryDto;
import com.suresell.mscoreapp.domain.model.OrderEditHistoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderEditHistoryMapper {
    OrderEditHistoryDto toDto(OrderEditHistoryEntity entity);
    List<OrderEditHistoryDto> toDtoList(List<OrderEditHistoryEntity> entities);
}
