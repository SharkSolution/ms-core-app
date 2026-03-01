package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.usecase.ManageAccountReceivableUseCase;
import com.suresell.mscoreapp.application.dto.AccountReceivable;
import com.suresell.mscoreapp.shared.enums.AccountStatus;
import com.suresell.mscoreapp.shared.enums.PaymentMethod;
import com.suresell.mscoreapp.application.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AccountReceivableService {

    private static final Logger logger = LoggerFactory.getLogger(AccountReceivableService.class);

    private final ManageAccountReceivableUseCase useCase;

    public AccountReceivableService(ManageAccountReceivableUseCase useCase) {
        this.useCase = useCase;
    }

    public AccountReceivable createAccount(CreateAccountRequest request) {
        logger.info("Creando nueva cuenta para: {}", request.getCustomerName());

        validateCreateAccountRequest(request);
        return useCase.createAccount(request);
    }

    public AccountReceivable addDebt(AddDebtRequest request) {
        logger.info("Agregando deuda de ${} para: {}", request.getAmount(), request.getCustomerDocument());

        validateAddDebtRequest(request);
        return useCase.addDebt(request);
    }

    public AccountReceivable makePayment(MakePaymentRequest request) {
        logger.info(" Procesando pago de ${} para: {}", request.getAmount(), request.getCustomerDocument());

        validateMakePaymentRequest(request);
        return useCase.makePayment(request);
    }

    public AccountReceivableDto getAccountByDocument(String customerDocument) {
        logger.debug("Consultando cuenta del cliente: {}", customerDocument);

        if (customerDocument == null || customerDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento del cliente es requerido");
        }

        return useCase.getAccountByDocument(customerDocument);
    }

    public List<AccountReceivableDto> getAccountsWithDebt() {
        logger.debug("bteniendo cuentas con deuda");
        return useCase.getAccountsWithDebt();
    }

    public List<AccountReceivableDto> getOverdueAccounts(int daysOverdue) {
        logger.debug("Obteniendo cuentas vencidas: {} días", daysOverdue);

        if (daysOverdue < 1 || daysOverdue > 365) {
            throw new IllegalArgumentException("Los días deben estar entre 1 y 365");
        }

        return useCase.getOverdueAccounts(daysOverdue);
    }

    public List<AccountReceivableDto> getAccountsByStatus(String statusName) {
        logger.debug("Obteniendo cuentas con estado: {}", statusName);

        try {
            AccountStatus status = AccountStatus.valueOf(statusName.toUpperCase());
            return useCase.getAccountsByStatus(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de cuenta inválido: " + statusName);
        }
    }

    public AccountReceivable suspendAccount(String customerDocument, String reason) {
        logger.info("Suspendiendo cuenta: {}", customerDocument);

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Razón de suspensión es requerida");
        }

        return useCase.suspendAccount(customerDocument, reason);
    }

    public AccountReceivable reactivateAccount(String customerDocument) {
        logger.info("Reactivando cuenta: {}", customerDocument);
        return useCase.reactivateAccount(customerDocument);
    }

    public AccountReceivable closeAccount(String customerDocument, String reason) {
        logger.info("Cerrando cuenta: {}", customerDocument);

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Razón de cierre es requerida");
        }

        return useCase.closeAccount(customerDocument, reason);
    }

    public AccountStatsResponse getStatistics() {
        logger.debug("Obteniendo estadísticas de cuentas");
        return useCase.getAccountStatistics();
    }

    public List<DebtTransactionDto> getTransactionHistory(String customerDocument, LocalDate startDate, LocalDate endDate) {
        logger.debug("Obteniendo historial de transacciones: {}", customerDocument);

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Fecha de inicio no puede ser mayor a fecha de fin");
        }

        return useCase.getTransactionHistory(customerDocument, startDate, endDate);
    }

    public List<AccountReceivableDto> searchAccounts(String searchTerm) {
        logger.debug("Buscando cuentas: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().length() < 2) {
            throw new IllegalArgumentException("Término de búsqueda debe tener al menos 2 caracteres");
        }

        return useCase.searchAccounts(searchTerm);
    }

    public AccountReceivable updateCreditLimit(String customerDocument, BigDecimal newCreditLimit) {
        logger.info("Actualizando límite de crédito: {}", customerDocument);

        if (newCreditLimit == null || newCreditLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El límite de crédito debe ser positivo");
        }

        return useCase.updateCreditLimit(customerDocument, newCreditLimit);
    }

    public void deleteAccount(String customerDocument) {
        logger.info("Eliminando cuenta: {}", customerDocument);

        if (customerDocument == null || customerDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento del cliente es requerido");
        }

        useCase.deleteAccount(customerDocument);
    }

    public boolean customerExists(String customerDocument) {
        return useCase.validateCustomerExists(customerDocument);
    }

    public BigDecimal getCustomerDebt(String customerDocument) {
        return useCase.getCustomerDebtAmount(customerDocument);
    }

    private void validateCreateAccountRequest(CreateAccountRequest request) {
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre del cliente es requerido");
        }

        if (request.getCustomerDocument() == null || request.getCustomerDocument().trim().isEmpty()) {
            throw new IllegalArgumentException("Documento del cliente es requerido");
        }

        if (request.getCreditLimit() != null && request.getCreditLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Límite de crédito no puede ser negativo");
        }
    }

    private void validateAddDebtRequest(AddDebtRequest request) {
        if (request.getCustomerDocument() == null || request.getCustomerDocument().trim().isEmpty()) {
            throw new IllegalArgumentException("Documento del cliente es requerido");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto debe ser positivo");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Descripción es requerida");
        }
    }

    private void validateMakePaymentRequest(MakePaymentRequest request) {
        if (request.getCustomerDocument() == null || request.getCustomerDocument().trim().isEmpty()) {
            throw new IllegalArgumentException("Documento del cliente es requerido");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto del pago debe ser positivo");
        }

        try {
            PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Método de pago inválido: " + request.getPaymentMethod());
        }
    }
}
