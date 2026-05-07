package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.PayrollCreateRequest;
import com.suresell.mscoreapp.application.usecase.ManagePayrollUseCase;
import com.suresell.mscoreapp.shared.enums.PayrollStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/payrolls")
@RequiredArgsConstructor
@Tag(name = "Payroll", description = "API for Payroll management")
public class PayrollController {

    private final ManagePayrollUseCase useCase;

    @PostMapping
    @Operation(summary = "Create payroll", description = "Registers a new payroll")
    public ResponseEntity<?> createPayroll(@Valid @RequestBody PayrollCreateRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(useCase.createPayroll(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get payrolls", description = "Retrieves all payrolls with optional filters")
    public ResponseEntity<?> getPayrolls(
            @RequestParam(required = false) PayrollStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            return ResponseEntity.ok(useCase.getPayrolls(status, startDate, endDate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payroll by ID", description = "Retrieves a payroll by its ID")
    public ResponseEntity<?> getPayrollById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(useCase.getPayrollById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve payroll", description = "Marks a pending payroll as paid")
    public ResponseEntity<?> approvePayroll(@PathVariable String id) {
        try {
            return ResponseEntity.ok(useCase.approvePayroll(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel payroll", description = "Marks a pending payroll as cancelled")
    public ResponseEntity<?> cancelPayroll(@PathVariable String id) {
        try {
            return ResponseEntity.ok(useCase.cancelPayroll(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payroll", description = "Deletes a pending payroll permanently")
    public ResponseEntity<?> deletePayroll(@PathVariable String id) {
        try {
            useCase.deletePayroll(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
