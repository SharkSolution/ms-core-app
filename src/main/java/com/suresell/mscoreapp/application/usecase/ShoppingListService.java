package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.usecase.ManageShoppingListUseCase;
import com.suresell.mscoreapp.application.dto.ShoppingItem;
import com.suresell.mscoreapp.application.dto.CreateShoppingItemRequest;
import com.suresell.mscoreapp.application.dto.ShoppingListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ShoppingListService {

    private final ManageShoppingListUseCase useCase;

    private static final Logger logger = LoggerFactory.getLogger(ShoppingListService.class);

    public ShoppingListService(ManageShoppingListUseCase useCase) {
        this.useCase = useCase;
    }

    public ShoppingListResponse getCurrentList() {
        return useCase.getActiveShoppingList();
    }

    public ShoppingItem createItem(CreateShoppingItemRequest request) throws Exception {
        validateRequest(request);
        return useCase.addItem(request);
    }

    public void updateQuantity(String itemId, BigDecimal quantity) {
        validateQuantity(quantity);
        logger.debug("testttttt: {}", quantity);

        useCase.updateItemQuantity(itemId, quantity);
    }

    public void purchaseItem(String itemId) {
        useCase.markItemAsPurchased(itemId);
    }

    public List<ShoppingItem> generateAutomaticShoppingList() {
        return useCase.generateAutomaticList();
    }

    public void deleteItem(String itemId) {
        useCase.removeItem(itemId);
    }

    private void validateRequest(CreateShoppingItemRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (request.getCurrentStock() == null) {
            throw new IllegalArgumentException("Current stock is required");
        }
        if (request.getMinStock() == null) {
            throw new IllegalArgumentException("Minimum stock is required");
        }
        if (request.getCurrentStock().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Current stock cannot be negative");
        }
        if (request.getMinStock().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }
    }

    private void validateQuantity(BigDecimal quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity is required");
        }
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}
