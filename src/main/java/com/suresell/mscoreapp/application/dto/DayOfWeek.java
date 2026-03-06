package com.suresell.mscoreapp.application.dto;

import java.time.LocalDate;

public enum DayOfWeek {
    MONDAY("Lunes"),
    TUESDAY("Martes"),
    WEDNESDAY("Miércoles"),
    THURSDAY("Jueves"),
    FRIDAY("Viernes"),
    SATURDAY("Sábado"),
    SUNDAY("Domingo");

    private final String spanishName;

    DayOfWeek(String spanishName) {
        this.spanishName = spanishName;
    }

    public String getSpanishName() {
        return spanishName;
    }

    public static DayOfWeek fromLocalDate(LocalDate date) {
        return values()[date.getDayOfWeek().getValue() - 1];
    }
}
