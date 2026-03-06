package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebtTransactionDto {
    private String id;
    private String type;
    private String typeDescription;
    private BigDecimal amount;
    private String description;
    private String reference;
    private String paymentMethod;
    private String paymentMethodDescription;
    private String transactionDate;
    private LocalDateTime createdAt;
}
