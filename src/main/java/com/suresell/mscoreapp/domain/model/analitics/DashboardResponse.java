package com.suresell.mscoreapp.domain.model.analitics;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        @JsonProperty("ventas_hoy") BigDecimal todaySales,
        @JsonProperty("ordenes_hoy") Long todayOrders,
        @JsonProperty("ticket_promedio") BigDecimal averageTicket,
        @JsonProperty("insumos_consumidos") Long inventoryConsumption,
        @JsonProperty("productos_estrella") List<TopProductDto> topProducts,
        @JsonProperty("metodologia_pagos") List<PaymentMethodDistDto> paymentMethods
) {}
