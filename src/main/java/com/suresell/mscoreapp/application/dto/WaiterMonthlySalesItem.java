package com.suresell.mscoreapp.application.dto;

import java.math.BigDecimal;

public record WaiterMonthlySalesItem(
        Long waiterId,
        String waiterName,
        long ordersCount,
        BigDecimal total
) {}
