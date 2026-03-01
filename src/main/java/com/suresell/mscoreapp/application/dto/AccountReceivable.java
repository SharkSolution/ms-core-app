package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.Setter;
import com.suresell.mscoreapp.shared.enums.AccountStatus;
import com.suresell.mscoreapp.shared.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AccountReceivable {
    private String id;
    private String customerName;
    private String customerDocument;
    private String customerPhone;
    private BigDecimal totalDebt;           // Deuda total actual
    private BigDecimal creditLimit;         // Límite de crédito permitido
    private AccountStatus status;           // Estado de la cuenta
    private LocalDate lastTransactionDate;  // Fecha de última transacción
    private String notes;                   // Notas adicionales
    private List<DebtTransaction> transactions; // Historial de transacciones
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AccountReceivable() {
        this.transactions = new ArrayList<>();
    }

    public static AccountReceivable createNew(String customerName, String customerDocument,
                                              String customerPhone, BigDecimal creditLimit) {
        AccountReceivable account = new AccountReceivable();
        account.setId(UUID.randomUUID().toString());
        account.setCustomerName(customerName);
        account.setCustomerDocument(customerDocument);
        account.setCustomerPhone(customerPhone);
        account.setTotalDebt(BigDecimal.ZERO);
        account.setCreditLimit(creditLimit != null ? creditLimit : BigDecimal.valueOf(100000));
        account.setStatus(AccountStatus.ACTIVE);
        account.setTransactions(new ArrayList<>());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        return account;
    }

    public void addDebt(BigDecimal amount, String description, String reference) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de la deuda debe ser positivo");
        }

        BigDecimal newTotal = this.totalDebt.add(amount);
        if (newTotal.compareTo(this.creditLimit) > 0) {
            throw new IllegalStateException("La operación excede el límite de crédito. Límite: " +
                    this.creditLimit + ", Nueva deuda: " + newTotal);
        }

        DebtTransaction transaction = DebtTransaction.createDebit(
                amount, description, reference
        );

        this.transactions.add(transaction);
        this.totalDebt = this.totalDebt.add(amount);
        this.lastTransactionDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();

        // Si la cuenta estaba cerrada reactivarla
        if (this.status == AccountStatus.CLOSED) {
            this.status = AccountStatus.ACTIVE;
        }
    }

    public void makePayment(BigDecimal amount, String description, PaymentMethod paymentMethod) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser positivo");
        }

        if (amount.compareTo(this.totalDebt) > 0) {
            throw new IllegalArgumentException("El pago no puede ser mayor a la deuda actual: " + this.totalDebt);
        }

        // Crear transacción de crédito (pago)
        DebtTransaction transaction = DebtTransaction.createCredit(
                amount, description, paymentMethod
        );

        this.transactions.add(transaction);
        this.totalDebt = this.totalDebt.subtract(amount);
        this.lastTransactionDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();

        // Si se saldó marcar como pagada
        if (this.totalDebt.compareTo(BigDecimal.ZERO) == 0) {
            this.status = AccountStatus.PAID;
        }
    }

    public void suspend(String reason) {
        this.status = AccountStatus.SUSPENDED;
        this.notes = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void reactivate() {
        if (this.totalDebt.compareTo(BigDecimal.ZERO) == 0) {
            this.status = AccountStatus.PAID;
        } else {
            this.status = AccountStatus.ACTIVE;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void close(String reason) {
        this.status = AccountStatus.CLOSED;
        this.notes = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canAddDebt(BigDecimal amount) {
        if (this.status != AccountStatus.ACTIVE) {
            return false;
        }
        return this.totalDebt.add(amount).compareTo(this.creditLimit) <= 0;
    }

    public boolean hasDebt() {
        return this.totalDebt.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getAvailableCredit() {
        return this.creditLimit.subtract(this.totalDebt);
    }

    public BigDecimal getCreditUtilization() {
        if (this.creditLimit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return this.totalDebt.divide(this.creditLimit, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public int getDaysWithDebt() {
        if (!hasDebt() || this.lastTransactionDate == null) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(this.lastTransactionDate, LocalDate.now());
    }

}