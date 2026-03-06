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
public class ValeraStatsResponse {
    private long totalValeras;
    private long activeValeras;
    private long usedValeras;
    private long expiredValeras;
    private long suspendedValeras;
    private BigDecimal totalSales;
    private BigDecimal totalDiscounts;
    private BigDecimal averageValeraValue;
    private BigDecimal totalOutstandingValue; // Valor pendiente de usar
}
