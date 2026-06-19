package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.CreateSupplierRequestDto;
import com.suresell.mscoreapp.application.usecase.ManageSupplierRequestUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/supplier-requests")
@Tag(name = "Solicitudes a Proveedor", description = "Solicitudes de pedido (cocina/Admin) y su revisión")
public class SupplierRequestController {

    private final ManageSupplierRequestUseCase useCase;

    public SupplierRequestController(ManageSupplierRequestUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @Operation(summary = "Crear solicitud de pedido", description = "Cocina (source=KITCHEN) o Admin crean una solicitud con sus productos")
    public ResponseEntity<?> create(@RequestBody CreateSupplierRequestDto dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar solicitudes", description = "Filtra por estado si se envía (PENDING/REVIEWED/ORDERED/CANCELLED)")
    public ResponseEntity<?> list(@RequestParam(value = "status", required = false) String status) {
        try {
            return ResponseEntity.ok(useCase.list(status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener solicitud por id")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(useCase.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de la solicitud")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(useCase.updateStatus(id, body.get("status")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
