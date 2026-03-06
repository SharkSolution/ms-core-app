package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.application.usecase.ShoppingItemEntityMapper;
import com.suresell.mscoreapp.application.dto.ShoppingItem;
import com.suresell.mscoreapp.shared.enums.ShoppingItemStatus;
import com.suresell.mscoreapp.domain.model.ShoppingItemEntity;
import com.suresell.mscoreapp.domain.port.out.ShoppingListRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.ShoppingListPanacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ShoppingListRepositoryImpl implements ShoppingListRepository {

    private final ShoppingItemEntityMapper mapper;
    private final ShoppingListPanacheRepository shoppingListJpaRepository;

    private static final Logger logger = LoggerFactory.getLogger(ShoppingListRepositoryImpl.class);

    public ShoppingListRepositoryImpl(ShoppingItemEntityMapper mapper, ShoppingListPanacheRepository shoppingListJpaRepository) {
        this.mapper = mapper;
        this.shoppingListJpaRepository = shoppingListJpaRepository;
    }

    @Override
    public List<ShoppingItem> findByStatus(ShoppingItemStatus status) {
        return shoppingListJpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShoppingItem> findBySupplyCategory(String supplyCategory) {
        return shoppingListJpaRepository.findBySupplyCategory(supplyCategory).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ShoppingItem> findById(String id) {
        logger.debug("🔍 Buscando item por ID: {}", id);
        return shoppingListJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public ShoppingItem save(ShoppingItem item) {
        logger.debug(" Guardando item: {} (ID: {})", item.getName(), item.getId());
        if (item.getId() != null) {
            return updateExistingItem(item);
        } else {
            item.setId(UUID.randomUUID().toString());
            return createNewItem(item);
        }
    }

    private ShoppingItem updateExistingItem(ShoppingItem item) {
        logger.debug("Actualizando item existente con ID: {}", item.getId());
        ShoppingItemEntity existingEntity = shoppingListJpaRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found for update: " + item.getId()));

        updateEntityFields(existingEntity, item);
        shoppingListJpaRepository.save(existingEntity);
        logger.info("Item actualizado exitosamente: {}", item.getId());
        return mapper.toDomain(existingEntity);
    }

    private void updateEntityFields(ShoppingItemEntity entity, ShoppingItem domain) {
        logger.debug("🔧 Actualizando campos de entidad para ID: {}", domain.getId());

        entity.setName(domain.getName());
        entity.setSupplyCategory(domain.getSupplyCategory());
        entity.setUnit(domain.getUnit());
        entity.setCurrentStock(domain.getCurrentStock());
        entity.setMinimumStock(domain.getMinimumStock());
        entity.setSuggestedQuantity(domain.getSuggestedQuantity());
        entity.setEstimatedCost(domain.getEstimatedCost());
        entity.setStatus(domain.getStatus());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }

    private ShoppingItem createNewItem(ShoppingItem item) {
        logger.debug("Creando nuevo item: {}", item.getName());
        ShoppingItemEntity newEntity = mapper.toEntity(item);
        shoppingListJpaRepository.save(newEntity);
        logger.info("Nuevo item creado con ID: {}", newEntity.getId());
        return mapper.toDomain(newEntity);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        shoppingListJpaRepository.deleteById(id);
    }

    @Override
    public List<ShoppingItem> findItemsNeedingRestock() {
        // Here we use currentStock <= minimumStock
        // This is complex for a simple method name, so we'll use the repository method
        return shoppingListJpaRepository.findAll().stream()
                .filter(item -> item.getCurrentStock().compareTo(item.getMinimumStock()) <= 0 && item.getStatus() == ShoppingItemStatus.PENDING)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
