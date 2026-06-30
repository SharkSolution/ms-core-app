package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.WaiterMonthlySalesResponse;
import com.suresell.mscoreapp.application.dto.WaiterSalesResponse;
import com.suresell.mscoreapp.application.usecase.GetWaiterMonthlySalesUseCase;
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
@Tag(name = "Waiter Sales", description = "Trazabilidad de ventas por mesero")
public class WaiterSalesController {

    private final GetWaiterSalesByDateUseCase getWaiterSalesByDateUseCase;
    private final GetWaiterMonthlySalesUseCase getWaiterMonthlySalesUseCase;

    public WaiterSalesController(GetWaiterSalesByDateUseCase getWaiterSalesByDateUseCase,
                                 GetWaiterMonthlySalesUseCase getWaiterMonthlySalesUseCase) {
        this.getWaiterSalesByDateUseCase = getWaiterSalesByDateUseCase;
        this.getWaiterMonthlySalesUseCase = getWaiterMonthlySalesUseCase;
    }

    @Operation(summary = "Ventas del día agrupadas por mesero y método de pago")
    @GetMapping
    public ResponseEntity<WaiterSalesResponse> getDailySales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(getWaiterSalesByDateUseCase.execute(target));
    }

    @Operation(summary = "Ventas acumuladas del mes por mesero (base para comisión)")
    @GetMapping("/monthly")
    public ResponseEntity<WaiterMonthlySalesResponse> getMonthlySales(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        LocalDate now = LocalDate.now();
        int targetYear = year != null ? year : now.getYear();
        int targetMonth = month != null ? month : now.getMonthValue();
        return ResponseEntity.ok(getWaiterMonthlySalesUseCase.execute(targetYear, targetMonth));
    }
}
