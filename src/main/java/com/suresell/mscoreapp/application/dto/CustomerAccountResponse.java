package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAccountResponse {
    private String customerDocument;
    private String customerName;
    private AccountReceivableDto account;
    private List<DebtTransactionDto> transactionHistory;
    private BigDecimal totalDebt;
    private BigDecimal totalPaid;
    private int totalTransactions;
}
