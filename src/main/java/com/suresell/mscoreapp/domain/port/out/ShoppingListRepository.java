package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.application.dto.ShoppingItem;
import com.suresell.mscoreapp.shared.enums.ShoppingItemStatus;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository {
    List<ShoppingItem> findByStatus(ShoppingItemStatus status);
    List<ShoppingItem> findBySupplyCategory(String supplyCategory);
    Optional<ShoppingItem> findById(String id);
    ShoppingItem save(ShoppingItem item);
    void deleteById(String id);
    List<ShoppingItem> findItemsNeedingRestock();
}
