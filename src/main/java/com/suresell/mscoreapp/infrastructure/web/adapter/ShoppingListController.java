package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.usecase.ShoppingListService;
import com.suresell.mscoreapp.application.dto.UpdateQuantityRequest;
import com.suresell.mscoreapp.application.dto.CreateShoppingItemRequest;
import com.suresell.mscoreapp.application.dto.ShoppingListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    private final ShoppingListService service;

    public ShoppingListController(ShoppingListService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getCurrentShoppingList() {
        ShoppingListResponse lista = service.getCurrentList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody CreateShoppingItemRequest request) throws Exception {
        var item = service.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/{itemId}/quantity")
    public ResponseEntity<?> updateQuantity(@PathVariable("itemId") String itemId,
                                            @RequestBody UpdateQuantityRequest request) {
        service.updateQuantity(itemId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{itemId}/purchase")
    public ResponseEntity<?> markAsPurchased(@PathVariable("itemId") String itemId) {
        service.purchaseItem(itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateAutomaticList() {
        var items = service.generateAutomaticShoppingList();
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable("itemId") String itemId) {
        service.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
