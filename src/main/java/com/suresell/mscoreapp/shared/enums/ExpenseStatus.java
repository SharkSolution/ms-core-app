package com.suresell.mscoreapp.shared.enums;

public enum ExpenseStatus {
    PENDING("Pendiente", "Gasto pendiente de aprobación"),
    APPROVED("Aprobado", "Gasto aprobado"),
    REJECTED("Rechazado", "Gasto rechazado");

    private final String displayName;
    private final String description;

    ExpenseStatus(String displayName, String description) {
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
