package com.suresell.mscoreapp.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SupplyDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal stock;
    private BigDecimal minStock;

    public SupplyDto(Long id, String name, BigDecimal price, BigDecimal stock, BigDecimal minStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.minStock = minStock;
    }

}