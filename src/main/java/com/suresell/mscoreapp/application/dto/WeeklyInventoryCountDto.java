package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyInventoryCountDto {
    private Long id;
    private Long supplyId;
    private String supplyName;
    private BigDecimal countedQuantity;
    private BigDecimal previousStock;
    private BigDecimal difference;
    private LocalDate weekStart;
    private String source;
    private String createdBy;
    private LocalDateTime createdAt;
}
