package com.suresell.mscoreapp.domain.model.analitics;

import java.math.BigDecimal;

public record WaiterMonthlySalesRowDto(
        Long waiterId,
        String waiterName,
        Long ordersCount,
        BigDecimal total
) {}
