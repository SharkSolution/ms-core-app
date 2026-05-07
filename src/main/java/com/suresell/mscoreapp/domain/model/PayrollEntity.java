package com.suresell.mscoreapp.domain.model;

import com.suresell.mscoreapp.shared.enums.PaymentMode;
import com.suresell.mscoreapp.shared.enums.PayrollStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "payrolls")
@AllArgsConstructor
@NoArgsConstructor
public class PayrollEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "document_id")
    private String documentId;

    @Column
    private String phone;

    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private PaymentMode paymentMode = PaymentMode.PER_SHIFT;

    @Column(name = "value_per_day", precision = 12, scale = 2)
    private BigDecimal valuePerDay;

    @Column(name = "worked_days")
    private Double workedDays;

    @Column(precision = 12, scale = 2)
    private BigDecimal commissions;

    @Column(precision = 12, scale = 2)
    private BigDecimal bonuses;

    @Column(name = "total_value", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalValue;

    @Column
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollStatus status = PayrollStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = PayrollStatus.PENDING;
        }
        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
