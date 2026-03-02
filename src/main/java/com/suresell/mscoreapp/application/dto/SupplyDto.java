package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal stock;
    private BigDecimal minStock;
    private String categoryName;
}