package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.OrderItemDto;
import com.suresell.mscoreapp.application.dto.OrderResponse;
import com.suresell.mscoreapp.domain.model.Order;
import com.suresell.mscoreapp.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
    List<OrderResponse> toResponseList(List<Order> orders);

    @Mapping(target = "productName", ignore = true)
    OrderItemDto toItemDto(OrderItem item);
}
