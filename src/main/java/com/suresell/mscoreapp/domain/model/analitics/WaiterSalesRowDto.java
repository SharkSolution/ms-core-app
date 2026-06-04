package com.suresell.mscoreapp.domain.model.analitics;

import java.math.BigDecimal;

public record WaiterSalesRowDto(
        Long waiterId,
        String waiterName,
        String paymentMethod,
        Long ordersCount,
        BigDecimal total
) {}
