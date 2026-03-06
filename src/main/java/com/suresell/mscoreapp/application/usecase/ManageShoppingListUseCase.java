package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.usecase.ShoppingListMapper;
import com.suresell.mscoreapp.application.dto.ShoppingItem;
import com.suresell.mscoreapp.shared.enums.ShoppingItemStatus;
import com.suresell.mscoreapp.domain.port.out.ShoppingListRepository;
import com.suresell.mscoreapp.application.dto.CreateShoppingItemRequest;
import com.suresell.mscoreapp.application.dto.ShoppingListResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ManageShoppingListUseCase {

    private final ShoppingListRepository repository;
    private final ShoppingListMapper mapper;

    public ManageShoppingListUseCase(ShoppingListRepository repository, ShoppingListMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ShoppingListResponse getActiveShoppingList() {
        List<ShoppingItem> itemsPending = repository.findByStatus(ShoppingItemStatus.PENDING);
        List<ShoppingItem> itemsPurchase = repository.findByStatus(ShoppingItemStatus.PURCHASED);

        List<ShoppingItem> itemsConcated = new ArrayList<>();
        itemsConcated.addAll(itemsPurchase);
        itemsConcated.addAll(itemsPending);
        return mapper.toResponse(itemsConcated);
    }

    public ShoppingItem addItem(CreateShoppingItemRequest request) throws Exception {
        ShoppingItem item = new ShoppingItem(
                request.getName(),
                request.getSupplyCategory(),
                request.getUnit(),
                request.getCurrentStock(),
                request.getMinimumStock()
        );
        try{
            return repository.save(item);
        }catch (Exception e) {
            throw new IllegalArgumentException("Error al guardar el item de la lista de compras.");
        }
    }

    public void updateItemQuantity(String itemId, BigDecimal quantity) {
        ShoppingItem item = repository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.updateQuantity(quantity);
        try{
            repository.save(item);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void markItemAsPurchased(String itemId) {
        ShoppingItem item = repository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.markAsPurchased();
        repository.save(item);
    }

    public List<ShoppingItem> generateAutomaticList() {
        return repository.findItemsNeedingRestock();
    }

    public void removeItem(String itemId) {
        repository.deleteById(itemId);
    }
}
