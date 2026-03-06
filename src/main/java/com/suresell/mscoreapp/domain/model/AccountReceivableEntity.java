package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.suresell.mscoreapp.shared.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts_receivable",
        indexes = {
                @Index(name = "idx_account_customer_doc", columnList = "customer_document"),
                @Index(name = "idx_account_status", columnList = "status"),
                @Index(name = "idx_account_last_transaction", columnList = "last_transaction_date")
        })
public class AccountReceivableEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_document", nullable = false, length = 20)
    private String customerDocument;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @Column(name = "total_debt", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalDebt;

    @Column(name = "credit_limit", precision = 12, scale = 2, nullable = false)
    private BigDecimal creditLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "last_transaction_date")
    private LocalDate lastTransactionDate;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relación con transacciones
    @OneToMany(mappedBy = "accountId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DebtTransactionEntity> transactions;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
