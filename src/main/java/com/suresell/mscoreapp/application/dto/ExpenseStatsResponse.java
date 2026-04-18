package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseStatsResponse {
    private long totalExpenses;
    private long pendingCount;
    private long approvedCount;
    private long rejectedCount;
    private BigDecimal totalAmount;
    private BigDecimal approvedAmount;
    private BigDecimal pendingAmount;
}
