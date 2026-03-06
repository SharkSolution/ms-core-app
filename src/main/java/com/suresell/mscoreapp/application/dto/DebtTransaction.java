package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.suresell.mscoreapp.shared.enums.PaymentMethod;
import com.suresell.mscoreapp.shared.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DebtTransaction {
    private String id;
    private TransactionType type;           // DEBIT (debe) o CREDIT (pago)
    private BigDecimal amount;
    private String description;
    private String reference;               // Referencia externa (número de orden, etc.)
    private PaymentMethod paymentMethod;    // Solo para créditos
    private LocalDate transactionDate;
    private LocalDateTime createdAt;

    public static DebtTransaction createDebit(BigDecimal amount, String description, String reference) {
        DebtTransaction transaction = new DebtTransaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setType(TransactionType.DEBIT);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setReference(reference);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setCreatedAt(LocalDateTime.now());
        return transaction;
    }

    public static DebtTransaction createCredit(BigDecimal amount, String description, PaymentMethod paymentMethod) {
        DebtTransaction transaction = new DebtTransaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setCreatedAt(LocalDateTime.now());
        return transaction;
    }
}
