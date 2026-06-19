package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequestItemDto {
    private Long id;
    private Long supplyId;
    private String productName;
    private String unit;
    private BigDecimal quantity;
}
