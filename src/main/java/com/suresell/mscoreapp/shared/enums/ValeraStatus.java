package com.suresell.mscoreapp.shared.enums;

public enum ValeraStatus {
    ACTIVE("Activa", "Valera activa y disponible para usar"),
    USED("Usada", "Valera completamente utilizada"),
    EXPIRED("Vencida", "Valera vencida por fecha"),
    SUSPENDED("Suspendida", "Valera temporalmente suspendida"),
    CANCELLED("Cancelada", "Valera cancelada permanentemente");

    private final String displayName;
    private final String description;

    ValeraStatus(String displayName, String description) {
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
