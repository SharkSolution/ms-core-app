package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.shared.enums.ShoppingItemStatus;
import com.suresell.mscoreapp.domain.model.ShoppingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListPanacheRepository extends JpaRepository<ShoppingItemEntity, String> {
    List<ShoppingItemEntity> findByStatus(ShoppingItemStatus status);
    List<ShoppingItemEntity> findBySupplyCategory(String supplyCategory);
    List<ShoppingItemEntity> findByCurrentStockLessThanEqualAndStatus(java.math.BigDecimal stock, ShoppingItemStatus status);
}
