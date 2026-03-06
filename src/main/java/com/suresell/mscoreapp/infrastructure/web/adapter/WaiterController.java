package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.CreateWaiterRequest;
import com.suresell.mscoreapp.application.dto.WaiterDto;
import com.suresell.mscoreapp.application.usecase.ManageWaiterUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waiters")
@RequiredArgsConstructor
@Tag(name = "Waiters Management", description = "Operaciones para gestionar meseros")
public class WaiterController {

    private final ManageWaiterUseCase manageWaiterUseCase;

    @GetMapping
    @Operation(summary = "Obtener todos los meseros")
    public List<WaiterDto> getAllWaiters() {
        return manageWaiterUseCase.getAllWaiters();
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener solo los meseros activos")
    public List<WaiterDto> getActiveWaiters() {
        return manageWaiterUseCase.getActiveWaiters();
    }

    @PostMapping
    @Operation(summary = "Añadir un nuevo mesero")
    public ResponseEntity<WaiterDto> addWaiter(@Valid @RequestBody CreateWaiterRequest request) {
        return new ResponseEntity<>(manageWaiterUseCase.addWaiter(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activar o desactivar un mesero")
    public WaiterDto updateStatus(@PathVariable Long id, @RequestParam boolean active) {
        return manageWaiterUseCase.updateWaiterStatus(id, active);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un mesero definitivamente")
    public ResponseEntity<Void> deleteWaiter(@PathVariable Long id) {
        manageWaiterUseCase.removeWaiter(id);
        return ResponseEntity.noContent().build();
    }
}
