package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.usecase.SupplyConsumptionMapper;
import com.suresell.mscoreapp.application.usecase.ManageSupplyConsumptionUseCase;
import com.suresell.mscoreapp.application.dto.SupplyConsumptionDto;
import com.suresell.mscoreapp.domain.model.SupplyConsumptionEntity;
import com.suresell.mscoreapp.domain.port.out.ISupplyConsumptionRepository;
import com.suresell.mscoreapp.domain.port.out.ISupplyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupplyConsumptionService implements ManageSupplyConsumptionUseCase {

    private final ISupplyConsumptionRepository inventoryConsumptionRepository;
    private final ISupplyRepository supplyRepository;
    private final SupplyConsumptionMapper mapper;

    public SupplyConsumptionService(ISupplyConsumptionRepository inventoryConsumptionRepository,
                                       ISupplyRepository supplyRepository,
                                       SupplyConsumptionMapper mapper) {
        this.inventoryConsumptionRepository = inventoryConsumptionRepository;
        this.supplyRepository = supplyRepository;
        this.mapper = mapper;
    }

    @Override
    public SupplyConsumptionDto create(SupplyConsumptionDto inventoryConsumption) {
        inventoryConsumption.setRegistrationDate(LocalDateTime.now());
        SupplyConsumptionEntity entity = mapper.toEntity(inventoryConsumption);
        inventoryConsumptionRepository.save(entity);
        supplyRepository.updateStock(entity.getSupplyId(), entity.getQuantity().negate());
        return mapper.toDto(entity);
    }

    @Override
    public List<SupplyConsumptionDto> getAll() {
        return mapper.toDtoList(inventoryConsumptionRepository.findAll());
    }
}
