package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.SupplyConsumptionDto;

import java.util.List;

public interface ManageSupplyConsumptionUseCase {

    SupplyConsumptionDto create(SupplyConsumptionDto inventoryConsumption);

    List<SupplyConsumptionDto> getAll();
}
