package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.domain.model.SupplyCategory;
import com.suresell.mscoreapp.domain.model.Supply;
import com.suresell.mscoreapp.domain.port.out.ISupplyCategoryRepository;
import com.suresell.mscoreapp.domain.port.out.ISupplyRepository;
import com.suresell.mscoreapp.application.dto.CreateSupplyDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateSupplyUseCase {

    private final ISupplyRepository supplyRepository;
    private final ISupplyCategoryRepository supplyCategoryRepository;

    public CreateSupplyUseCase(ISupplyRepository supplyRepository, ISupplyCategoryRepository supplyCategoryRepository) {
        this.supplyRepository = supplyRepository;
        this.supplyCategoryRepository = supplyCategoryRepository;
    }

    public void execute(CreateSupplyDto dto) {
        SupplyCategory supplyCategory = supplyCategoryRepository.findById(dto.getSupplyCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("SupplyCategory not found"));

        Supply supply = new Supply();
        supply.setName(dto.getName());
        supply.setPrice(dto.getPrice());
        supply.setStock(dto.getStock());
        supply.setMinStock(dto.getMinStock());
        supply.setSupplyCategory(supplyCategory);

        supplyRepository.save(supply);
    }

    public List<Supply> findAll() {
        return supplyRepository.findAll();
    }
}
