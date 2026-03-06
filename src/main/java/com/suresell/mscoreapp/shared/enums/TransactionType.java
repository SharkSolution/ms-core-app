package com.suresell.mscoreapp.shared.enums;

public enum TransactionType {
    DEBIT("Débito", "Aumento de deuda"),
    CREDIT("Crédito", "Pago/Abono");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
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