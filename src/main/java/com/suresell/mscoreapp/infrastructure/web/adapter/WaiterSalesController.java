package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.WaiterSalesResponse;
import com.suresell.mscoreapp.application.usecase.GetWaiterSalesByDateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/waiter-sales")
@Tag(name = "Waiter Sales", description = "Trazabilidad de ventas diarias por mesero")
public class WaiterSalesController {

    private final GetWaiterSalesByDateUseCase getWaiterSalesByDateUseCase;

    public WaiterSalesController(GetWaiterSalesByDateUseCase getWaiterSalesByDateUseCase) {
        this.getWaiterSalesByDateUseCase = getWaiterSalesByDateUseCase;
    }

    @Operation(summary = "Ventas del día agrupadas por mesero y método de pago")
    @GetMapping
    public ResponseEntity<WaiterSalesResponse> getDailySales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(getWaiterSalesByDateUseCase.execute(target));
    }
}
