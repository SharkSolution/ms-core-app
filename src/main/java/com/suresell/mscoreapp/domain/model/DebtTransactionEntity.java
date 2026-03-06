package com.suresell.mscoreapp.domain.model;

import com.suresell.mscoreapp.shared.enums.PaymentMethod;
import com.suresell.mscoreapp.shared.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "debt_transactions",
        indexes = {
                @Index(name = "idx_transaction_account", columnList = "account_id"),
                @Index(name = "idx_transaction_date", columnList = "transaction_date"),
                @Index(name = "idx_transaction_type", columnList = "type")
        })
public class DebtTransactionEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "reference", length = 100)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
