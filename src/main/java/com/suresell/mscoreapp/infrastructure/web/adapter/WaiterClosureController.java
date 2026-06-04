package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.WaiterClosurePreviewResponse;
import com.suresell.mscoreapp.application.dto.WaiterClosureRequest;
import com.suresell.mscoreapp.application.dto.WaiterClosureResponse;
import com.suresell.mscoreapp.application.usecase.WaiterClosureUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/waiter-closures")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Waiter Closures", description = "Endpoints para el Cierre de Caja de Meseros")
public class WaiterClosureController {

    private final WaiterClosureUseCase waiterClosureUseCase;

    public WaiterClosureController(WaiterClosureUseCase waiterClosureUseCase) {
        this.waiterClosureUseCase = waiterClosureUseCase;
    }

    @GetMapping("/preview")
    @Operation(summary = "Obtener vista previa del cierre de caja del mesero hoy")
    public ResponseEntity<WaiterClosurePreviewResponse> getWaiterClosurePreview(@RequestParam String waiterId) {
        WaiterClosurePreviewResponse preview = waiterClosureUseCase.getWaiterClosurePreview(waiterId);
        return ResponseEntity.ok(preview);
    }

    @PostMapping
    @Operation(summary = "Registrar cierre de caja físico de un mesero")
    public ResponseEntity<WaiterClosureResponse> executeWaiterClosure(@Valid @RequestBody WaiterClosureRequest request) {
        WaiterClosureResponse response = waiterClosureUseCase.executeWaiterClosure(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
