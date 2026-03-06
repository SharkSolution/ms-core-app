package com.suresell.mscoreapp.domain.model.analitics;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PeakHourDto(
        @JsonProperty("hora") Integer hour,
        @JsonProperty("cantidad_ordenes") Long ordersCount
) {}