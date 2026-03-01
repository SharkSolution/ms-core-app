package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.usecase.SupplyConsumptionService;
import com.suresell.mscoreapp.application.dto.SupplyConsumptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory-consumptions")
public class SupplyConsumptionController {

    private final SupplyConsumptionService inventoryConsumptionService;

    public SupplyConsumptionController(SupplyConsumptionService inventoryConsumptionService) {
        this.inventoryConsumptionService = inventoryConsumptionService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SupplyConsumptionDto inventoryConsumption) {
        SupplyConsumptionDto created = inventoryConsumptionService.create(inventoryConsumption);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<SupplyConsumptionDto> consumptions = inventoryConsumptionService.getAll();
        return ResponseEntity.ok(consumptions);
    }
}
