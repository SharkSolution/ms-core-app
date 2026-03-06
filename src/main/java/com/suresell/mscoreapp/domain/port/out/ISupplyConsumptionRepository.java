package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.SupplyConsumptionEntity;

import java.util.List;

public interface ISupplyConsumptionRepository {

    SupplyConsumptionEntity save(SupplyConsumptionEntity inventoryConsumptionEntity);

    List<SupplyConsumptionEntity> findAll();
}
