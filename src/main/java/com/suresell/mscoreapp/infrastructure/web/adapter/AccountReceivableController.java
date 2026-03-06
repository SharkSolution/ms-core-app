package com.suresell.mscoreapp.infrastructure.web.adapter;

import jakarta.validation.Valid;
import com.suresell.mscoreapp.application.usecase.AccountReceivableService;
import com.suresell.mscoreapp.application.dto.AccountReceivable;
import com.suresell.mscoreapp.application.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts-receivable")
public class AccountReceivableController {

    private static final Logger logger = LoggerFactory.getLogger(AccountReceivableController.class);

    private final AccountReceivableService service;

    public AccountReceivableController(AccountReceivableService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        logger.info("POST /api/accounts-receivable - Creando cuenta para: {}", request.getCustomerName());

        try {
            AccountReceivable account = service.createAccount(request);
            logger.info("Cuenta creada exitosamente para: {}", account.getCustomerName());
            return ResponseEntity.status(HttpStatus.CREATED).body(account);
        } catch (IllegalArgumentException e) {
            logger.warn("Datos inválidos para crear cuenta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado creando cuenta", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add-debt")
    public ResponseEntity<?> addDebt(@Valid @RequestBody AddDebtRequest request) {
        logger.info("POST /api/accounts-receivable/add-debt - Agregando deuda de ${} para: {}",
                request.getAmount(), request.getCustomerDocument());

        try {
            AccountReceivable account = service.addDebt(request);
            logger.info("Deuda agregada exitosamente. Nueva deuda total: ${}", account.getTotalDebt());
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            logger.warn("Error agregando deuda: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para agregar deuda: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado agregando deuda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/make-payment")
    public ResponseEntity<?> makePayment(@Valid @RequestBody MakePaymentRequest request) {
        logger.info("POST /api/accounts-receivable/make-payment - Procesando pago de ${} para: {}",
                request.getAmount(), request.getCustomerDocument());

        try {
            AccountReceivable account = service.makePayment(request);
            logger.info("Pago procesado exitosamente. Deuda restante: ${}", account.getTotalDebt());
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            logger.warn("Error procesando pago: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para procesar pago: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado procesando pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{customerDocument}")
    public ResponseEntity<?> getAccountByDocument(@PathVariable("customerDocument") String customerDocument) {
        logger.info("🔍 GET /api/accounts-receivable/customer/{} - Consultando cuenta", customerDocument);

        try {
            AccountReceivableDto account = service.getAccountByDocument(customerDocument);
            logger.info("Cuenta encontrada: {}", customerDocument);
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            logger.warn("Cliente no encontrado: {}", customerDocument);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error consultando cuenta: {}", customerDocument, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/with-debt")
    public ResponseEntity<?> getAccountsWithDebt() {
        logger.info("📋 GET /api/accounts-receivable/with-debt - Consultando cuentas con deuda");

        try {
            List<AccountReceivableDto> accounts = service.getAccountsWithDebt();
            logger.info("{} cuentas con deuda encontradas", accounts.size());
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            logger.error("Error consultando cuentas con deuda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueAccounts(@RequestParam(value = "days", defaultValue = "30") int days) {
        logger.info("⏰ GET /api/accounts-receivable/overdue?days={} - Cuentas vencidas", days);

        try {
            List<AccountReceivableDto> accounts = service.getOverdueAccounts(days);
            logger.info("{} cuentas vencidas encontradas", accounts.size());
            return ResponseEntity.ok(accounts);
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetro de días inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error consultando cuentas vencidas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAccountsByStatus(@PathVariable("status") String status) {
        logger.info("GET /api/accounts-receivable/status/{} - Cuentas por estado", status);

        try {
            List<AccountReceivableDto> accounts = service.getAccountsByStatus(status);
            logger.info("{} cuentas con estado {} encontradas", accounts.size(), status);
            return ResponseEntity.ok(accounts);
        } catch (IllegalArgumentException e) {
            logger.warn("Estado inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error consultando cuentas vencidas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/customer/{customerDocument}/close")
    public ResponseEntity<?> closeAccount(@PathVariable("customerDocument") String customerDocument,
                                          @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        logger.info("PUT /api/accounts-receivable/customer/{}/close - Cerrando cuenta", customerDocument);

        try {
            AccountReceivable account = service.closeAccount(customerDocument, reason);
            logger.info("Cuenta {} cerrada", customerDocument);
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            logger.warn("Error cerrando cuenta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para cerrar cuenta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado cerrando cuenta: {}", customerDocument, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/customer/{customerDocument}/credit-limit")
    public ResponseEntity<?> updateCreditLimit(@PathVariable("customerDocument") String customerDocument,
                                               @RequestBody Map<String, BigDecimal> request) {
        BigDecimal newLimit = request.get("creditLimit");
        logger.info("PUT /api/accounts-receivable/customer/{}/credit-limit - Nuevo límite: ${}",
                customerDocument, newLimit);

        try {
            AccountReceivable account = service.updateCreditLimit(customerDocument, newLimit);
            logger.info("Límite de crédito actualizado para: {}", customerDocument);
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            logger.warn("Error actualizando límite: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado actualizando límite: {}", customerDocument, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{customerDocument}/transactions")
    public ResponseEntity<?> getTransactionHistory(@PathVariable("customerDocument") String customerDocument,
                                                   @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                   @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("GET /api/accounts-receivable/customer/{}/transactions - Historial", customerDocument);

        try {
            List<DebtTransactionDto> transactions = service.getTransactionHistory(customerDocument, startDate, endDate);
            logger.info("{} transacciones encontradas para: {}", transactions.size(), customerDocument);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            logger.warn("Error en historial de transacciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error obteniendo historial: {}", customerDocument, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{customerDocument}/debt-amount")
    public ResponseEntity<?> getCustomerDebt(@PathVariable("customerDocument") String customerDocument) {
        logger.info("GET /api/accounts-receivable/customer/{}/debt-amount - Consulta de deuda", customerDocument);

        try {
            BigDecimal debtAmount = service.getCustomerDebt(customerDocument);
            logger.info("Deuda consultada para: {} = ${}", customerDocument, debtAmount);
            return ResponseEntity.ok(Map.of(
                    "customerDocument", customerDocument,
                    "debtAmount", debtAmount
            ));
        } catch (Exception e) {
            logger.error("Error consultando deuda: {}", customerDocument, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        logger.info("GET /api/accounts-receivable/statistics - Estadísticas generales");

        try {
            AccountStatsResponse stats = service.getStatistics();
            logger.info("Estadísticas generadas exitosamente");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error generando estadísticas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
