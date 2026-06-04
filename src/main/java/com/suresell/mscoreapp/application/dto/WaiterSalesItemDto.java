package com.suresell.mscoreapp.application.dto;

import java.math.BigDecimal;
import java.util.Map;

public record WaiterSalesItemDto(
        Long waiterId,
        String waiterName,
        long ordersCount,
        BigDecimal total,
        Map<String, BigDecimal> breakdown
) {}
