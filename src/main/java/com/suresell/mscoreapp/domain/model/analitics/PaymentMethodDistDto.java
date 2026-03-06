package com.suresell.mscoreapp.domain.model.analitics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PaymentMethodDistDto(
        @JsonProperty("metodo") String method,
        @JsonProperty("cantidad_trx") Long trxCount,
        @JsonProperty("total_dinero") BigDecimal totalMoney
) {}
