package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatsResponse {
    private long totalAccounts;
    private long activeAccounts;
    private long accountsWithDebt;
    private long suspendedAccounts;
    private BigDecimal totalDebtAmount;
    private BigDecimal averageDebtAmount;
    private BigDecimal totalCreditLimit;
    private BigDecimal totalAvailableCredit;
    private long overdueAccounts;
    private BigDecimal overdueAmount;
}
