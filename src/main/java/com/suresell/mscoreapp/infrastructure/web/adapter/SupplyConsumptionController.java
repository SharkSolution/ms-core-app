package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.usecase.SupplyConsumptionService;
import com.suresell.mscoreapp.application.dto.SupplyConsumptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supply-consumptions")
@Tag(name = "Gestión de Consumos (Materia Prima)", description = "Endpoints para registrar y consultar salidas de stock de insumos")
public class SupplyConsumptionController {

    private final SupplyConsumptionService supplyConsumptionService;

    public SupplyConsumptionController(SupplyConsumptionService supplyConsumptionService) {
        this.supplyConsumptionService = supplyConsumptionService;
    }

    @PostMapping
    @Operation(summary = "Registrar un consumo", description = "Registra la salida de un insumo y descuenta automáticamente el stock")
    public ResponseEntity<?> create(@RequestBody SupplyConsumptionDto supplyConsumption) {
        SupplyConsumptionDto created = supplyConsumptionService.create(supplyConsumption);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar historial de consumos", description = "Obtiene todos los registros de salidas de insumos ordenados por fecha")
    public ResponseEntity<?> getAll() {
        List<SupplyConsumptionDto> consumptions = supplyConsumptionService.getAll();
        return ResponseEntity.ok(consumptions);
    }
}
