package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.OrderResponse;
import com.suresell.mscoreapp.domain.model.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
    List<OrderResponse> toResponseList(List<Order> orders);
}
