package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateSupplierRequestItemDto {
    private Long supplyId;        // opcional: enlace al maestro Supply
    private String productName;   // requerido (puede venir del Supply o libre)
    private String unit;          // p.ej. "kg", "caja"
    private BigDecimal quantity;
}
