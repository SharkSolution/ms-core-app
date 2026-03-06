package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.OrderEditHistoryDto;
import com.suresell.mscoreapp.application.usecase.GetOrderEditHistoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/edit-history")
@RequiredArgsConstructor
@Tag(name = "Order Audit", description = "Endpoints para consultar el historial de ediciones de las órdenes")
public class OrderEditHistoryController {

    private final GetOrderEditHistoryUseCase getOrderEditHistoryUseCase;

    @GetMapping
    @Operation(summary = "Consultar el historial de ediciones de las órdenes", 
               description = "Permite filtrar por orderId y soporta paginación")
    public Page<OrderEditHistoryDto> getEditHistory(
            @Parameter(description = "ID de la orden (opcional)") 
            @RequestParam(required = false) Long orderId,
            
            @Parameter(description = "Número de página (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Cantidad de registros por página") 
            @RequestParam(defaultValue = "10") int size) {
        
        // Ordenar por fecha de edición de forma descendente por defecto
        Pageable pageable = PageRequest.of(page, size, Sort.by("editedAt").descending());
        
        return getOrderEditHistoryUseCase.getHistory(orderId, pageable);
    }
}
