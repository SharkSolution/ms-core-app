package com.suresell.mscoreapp.shared.enums;

public enum ValeraType {
    ALMUERZO("Almuerzo", "Valera para almuerzos"),
    DESAYUNO("Desayuno", "Valera para desayunos"),
    EJECUTIVO("Ejecutivo", "Valera ejecutiva premium");

    private final String displayName;
    private final String description;

    ValeraType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
