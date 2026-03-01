package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.domain.model.SupplyCategory;
import com.suresell.mscoreapp.domain.port.out.ISupplyCategoryRepository;
import com.suresell.mscoreapp.application.dto.CreateSupplyCategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CreateSupplyCategoryUseCase {

    private final ISupplyCategoryRepository supplyCategoryRepository;

    public CreateSupplyCategoryUseCase(ISupplyCategoryRepository supplyCategoryRepository) {
        this.supplyCategoryRepository = supplyCategoryRepository;
    }

    public void execute(CreateSupplyCategoryDto dto) {
        SupplyCategory supplyCategory = new SupplyCategory();
        supplyCategory.setName(dto.getName());
        supplyCategoryRepository.save(supplyCategory);
    }
}
