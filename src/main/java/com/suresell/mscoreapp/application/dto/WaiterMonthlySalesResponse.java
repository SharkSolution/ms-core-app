package com.suresell.mscoreapp.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record WaiterMonthlySalesResponse(
        int year,
        int month,
        BigDecimal grandTotal,
        long totalOrders,
        List<WaiterMonthlySalesItem> waiters,
        WaiterMonthlySalesItem unassigned
) {}
