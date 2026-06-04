package com.suresell.mscoreapp.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record WaiterSalesResponse(
        LocalDate date,
        BigDecimal grandTotal,
        long totalOrders,
        Map<String, BigDecimal> grandTotalByMethod,
        List<WaiterSalesItemDto> waiters,
        WaiterSalesItemDto unassigned
) {}
