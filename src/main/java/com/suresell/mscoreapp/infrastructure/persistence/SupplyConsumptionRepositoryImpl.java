package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.SupplyConsumptionEntity;
import com.suresell.mscoreapp.domain.port.out.ISupplyConsumptionRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.SupplyConsumptionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SupplyConsumptionRepositoryImpl implements ISupplyConsumptionRepository {

    private final SupplyConsumptionJpaRepository panacheRepository;

    public SupplyConsumptionRepositoryImpl(SupplyConsumptionJpaRepository panacheRepository) {
        this.panacheRepository = panacheRepository;
    }

    @Override
    @Transactional
    public SupplyConsumptionEntity save(SupplyConsumptionEntity inventoryConsumptionEntity) {
        return panacheRepository.save(inventoryConsumptionEntity);
    }

    @Override
    public List<SupplyConsumptionEntity> findAll() {
        return panacheRepository.findAll();
    }
}
