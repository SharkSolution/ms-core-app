package com.suresell.mscoreapp.infrastructure.web;

import com.suresell.mscoreapp.application.dto.CreateQrPaymentRequest;
import com.suresell.mscoreapp.application.dto.QrPaymentDto;
import com.suresell.mscoreapp.application.usecase.QrPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/qr-payments")
@RequiredArgsConstructor
@Tag(name = "QR Payments", description = "Endpoints para la gestión centralizada de pagos por QR (Admin)")
public class QrPaymentController {

    private final QrPaymentService qrPaymentService;

    @PostMapping
    @Operation(summary = "Registrar o actualizar el pago QR de un día")
    public ResponseEntity<QrPaymentDto> registerQrPayment(@Valid @RequestBody CreateQrPaymentRequest request) {
        QrPaymentDto response = qrPaymentService.registerOrUpdateQrPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date")
    @Operation(summary = "Obtener el pago QR registrado para una fecha específica")
    public ResponseEntity<QrPaymentDto> getQrPaymentByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return qrPaymentService.getQrPaymentByDate(date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
