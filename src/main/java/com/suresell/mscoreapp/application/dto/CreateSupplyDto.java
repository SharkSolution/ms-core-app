package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateSupplyDto {
    private String name;
    private BigDecimal price;
    private BigDecimal stock;
    private BigDecimal minStock;
    private Long supplyCategoryId;

}