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
public class ValeraDto {
    private String id;
    private String code;
    private String customerName;
    private String customerDocument;
    private String customerPhone;
    private String type;
    private String typeDescription;
    private Integer totalMeals;
    private Integer remainingMeals;
    private Integer usedMeals;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private BigDecimal discount;
    private BigDecimal finalPrice;
    private String purchaseDate;
    private String expirationDate;
    private String status;
    private String statusDescription;
    private String notes;
    private BigDecimal utilizationPercentage;
    private BigDecimal savingsAmount;
    private BigDecimal savingsPercentage;
    private boolean canUse;
    private boolean expired;
    private int daysUntilExpiration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
