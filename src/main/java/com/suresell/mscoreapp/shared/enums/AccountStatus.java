package com.suresell.mscoreapp.shared.enums;

public enum AccountStatus {
    ACTIVE("Activa", "Cuenta activa para nuevas deudas"),
    PAID("Pagada", "Cuenta sin deudas pendientes"),
    SUSPENDED("Suspendida", "Cuenta temporalmente suspendida"),
    CLOSED("Cerrada", "Cuenta cerrada permanentemente");

    private final String displayName;
    private final String description;

    AccountStatus(String displayName, String description) {
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
