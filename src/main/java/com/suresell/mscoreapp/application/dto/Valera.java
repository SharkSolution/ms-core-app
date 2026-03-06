package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.suresell.mscoreapp.shared.enums.ValeraStatus;
import com.suresell.mscoreapp.shared.enums.ValeraType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Valera {
    private String id;
    private String code;                    // Código único de la valera (ej: VAL-001234)
    private String customerName;           // Nombre del cliente
    private String customerDocument;       // Documento del cliente
    private String customerPhone;          // Teléfono del cliente
    private ValeraType type;              // Tipo de valera (ALMUERZO, DESAYUNO, etc.)
    private Integer totalMeals;           // Total de comidas incluidas
    private Integer remainingMeals;       // Comidas restantes
    private BigDecimal unitPrice;         // Precio unitario regular
    private BigDecimal totalValue;        // Valor total de la valera
    private BigDecimal discount;          // Descuento aplicado
    private BigDecimal finalPrice;        // Precio final pagado
    private LocalDate purchaseDate;       // Fecha de compra
    private LocalDate expirationDate;     // Fecha de vencimiento
    private ValeraStatus status;          // Estado de la valera
    private String notes;                 // Notas adicionales
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Factory method para crear nueva valera
    public static Valera createNew(String customerName, String customerDocument, String customerPhone,
                                   ValeraType type, Integer totalMeals, BigDecimal unitPrice,
                                   Integer validityDays, BigDecimal discountPercentage) {
        Valera valera = new Valera();
        valera.setId(UUID.randomUUID().toString());
        valera.setCode(generateCode());
        valera.setCustomerName(customerName);
        valera.setCustomerDocument(customerDocument);
        valera.setCustomerPhone(customerPhone);
        valera.setType(type);
        valera.setTotalMeals(totalMeals);
        valera.setRemainingMeals(totalMeals);
        valera.setUnitPrice(unitPrice);
        valera.setTotalValue(unitPrice.multiply(BigDecimal.valueOf(totalMeals)));
        valera.setDiscount(calculateDiscount(valera.getTotalValue(), discountPercentage));
        valera.setFinalPrice(valera.getTotalValue().subtract(valera.getDiscount()));
        valera.setPurchaseDate(LocalDate.now());
        valera.setExpirationDate(LocalDate.now().plusDays(validityDays));
        valera.setStatus(ValeraStatus.ACTIVE);
        valera.setCreatedAt(LocalDateTime.now());
        valera.setUpdatedAt(LocalDateTime.now());
        return valera;
    }

    private static String generateCode() {
        // Generar código único: VAL-YYYYMMDD-####
        String dateStr = LocalDate.now().toString().replace("-", "");
        String randomStr = String.format("%04d", (int) (Math.random() * 10000));
        return "VAL-" + dateStr + "-" + randomStr;
    }

    private static BigDecimal calculateDiscount(BigDecimal totalValue, BigDecimal discountPercentage) {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return totalValue.multiply(discountPercentage.divide(BigDecimal.valueOf(100)));
    }

    public void useMeal() {
        if (this.status != ValeraStatus.ACTIVE) {
            throw new IllegalStateException("Valera no está activa");
        }
        if (this.remainingMeals <= 0) {
            throw new IllegalStateException("No quedan comidas disponibles");
        }
        if (LocalDate.now().isAfter(this.expirationDate)) {
            throw new IllegalStateException("Valera expirada");
        }

        this.remainingMeals--;
        this.updatedAt = LocalDateTime.now();

        // Si no quedan comidas, marcar como usada
        if (this.remainingMeals == 0) {
            this.status = ValeraStatus.USED;
        }
    }

    public void suspend(String reason) {
        this.status = ValeraStatus.SUSPENDED;
        this.notes = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void reactivate() {
        if (LocalDate.now().isAfter(this.expirationDate)) {
            throw new IllegalStateException("No se puede reactivar una valera expirada");
        }
        this.status = ValeraStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        this.status = ValeraStatus.CANCELLED;
        this.notes = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = ValeraStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(this.expirationDate);
    }

    public boolean canUse() {
        return this.status == ValeraStatus.ACTIVE &&
                this.remainingMeals > 0 &&
                !isExpired();
    }

    public BigDecimal getSavingsAmount() {
        return this.discount;
    }

    public BigDecimal getSavingsPercentage() {
        if (this.totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return this.discount.divide(this.totalValue, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public Integer getUsedMeals() {
        return this.totalMeals - this.remainingMeals;
    }

    public BigDecimal getUtilizationPercentage() {
        if (this.totalMeals == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(getUsedMeals())
                .divide(BigDecimal.valueOf(this.totalMeals), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
