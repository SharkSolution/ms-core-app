package com.suresell.mscoreapp.application.dto;

import com.suresell.mscoreapp.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long idOrder;
    private String pagerColor;
    private String pagerNumber;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private BigDecimal total;
    private OrderStatus status;
    private String paymentMethod;
    private List<OrderItemDto> items;
}
