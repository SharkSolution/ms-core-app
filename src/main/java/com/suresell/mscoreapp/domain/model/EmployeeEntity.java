package com.suresell.mscoreapp.domain.model;

import com.suresell.mscoreapp.shared.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Maestra de empleados. Guarda el salario base predefinido de cada empleado segun
 * UNA modalidad de pago ({@link PaymentMode}), de modo que la nomina pueda pre-llenar
 * el pago sin retipear datos. {@code baseSalary} es el valor que corresponde a esa
 * modalidad (valor por turno, valor por hora o salario mensual).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "phone")
    private String phone;

    // Cargo del empleado (Mesero, Cocinero, Cajero, etc.).
    @Column(name = "role", nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private PaymentMode paymentMode = PaymentMode.PER_SHIFT;

    // Valor base del salario segun la modalidad: por turno, por hora o mensual.
    @Column(name = "base_salary", precision = 12, scale = 2, nullable = false)
    private BigDecimal baseSalary = BigDecimal.ZERO;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
        if (paymentMode == null) {
            paymentMode = PaymentMode.PER_SHIFT;
        }
        if (baseSalary == null) {
            baseSalary = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
