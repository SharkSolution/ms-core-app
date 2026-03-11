package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.OrderResponse;
import com.suresell.mscoreapp.application.usecase.ManageOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders Management", description = "Operaciones para la administración de órdenes")
public class OrderController {

    private final ManageOrderUseCase manageOrderUseCase;

    @GetMapping
    @Operation(summary = "Listar y filtrar órdenes con paginación")
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) Long idOrder,
            @RequestParam(required = false) String pagerColor,
            @RequestParam(required = false) String pagerNumber,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(manageOrderUseCase.getOrders(idOrder, pagerColor, pagerNumber, pageable));
    }
}
