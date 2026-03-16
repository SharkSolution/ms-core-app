package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.usecase.ShoppingListMapper;
import com.suresell.mscoreapp.application.dto.ShoppingItem;
import com.suresell.mscoreapp.shared.enums.ShoppingItemStatus;
import com.suresell.mscoreapp.domain.port.out.ShoppingListRepository;
import com.suresell.mscoreapp.application.dto.CreateShoppingItemRequest;
import com.suresell.mscoreapp.application.dto.ShoppingListResponse;
import com.suresell.mscoreapp.domain.port.out.ISupplyCategoryRepository;
import com.suresell.mscoreapp.domain.model.SupplyCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ManageShoppingListUseCase {

    private final ShoppingListRepository repository;
    private final ShoppingListMapper mapper;
    private final ISupplyCategoryRepository categoryRepository;

    public ManageShoppingListUseCase(ShoppingListRepository repository, 
                                    ShoppingListMapper mapper,
                                    ISupplyCategoryRepository categoryRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
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
        String categoryName = "Sin categoría";
        
        if (request.getCategoryId() != null) {
            try {
                Object rawId = request.getCategoryId();
                Long id = null;
                
                if (rawId instanceof Number) {
                    id = ((Number) rawId).longValue();
                } else if (rawId instanceof String) {
                    try {
                        id = Long.parseLong((String) rawId);
                    } catch (NumberFormatException nfe) {
                        // Es un nombre de categoría directamente
                        categoryName = (String) rawId;
                    }
                }
                
                if (id != null) {
                    Optional<SupplyCategory> category = categoryRepository.findById(id);
                    if (category.isPresent()) {
                        categoryName = category.get().getName();
                    } else {
                        // Si el ID no existe, usamos el ID como nombre temporal
                        categoryName = "ID: " + id;
                    }
                }
            } catch (Exception e) {
                categoryName = String.valueOf(request.getCategoryId());
            }
        }

        ShoppingItem item = new ShoppingItem(
                request.getName(),
                categoryName,
                request.getUnit(),
                request.getCurrentStock(),
                request.getMinStock()
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
