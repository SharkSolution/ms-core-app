package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountReceivableDto {
    private String id;
    private String customerName;
    private String customerDocument;
    private String customerPhone;
    private BigDecimal totalDebt;
    private BigDecimal creditLimit;
    private BigDecimal availableCredit;
    private String status;
    private String statusDescription;
    private String lastTransactionDate;
    private String notes;
    private BigDecimal creditUtilization;
    private int daysWithDebt;
    private boolean hasDebt;
    private boolean canAddDebt;
    private int totalTransactions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DebtTransactionDto> recentTransactions;
}
