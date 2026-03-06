package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.domain.model.SupplyCategory;
import com.suresell.mscoreapp.domain.model.Supply;
import com.suresell.mscoreapp.domain.port.out.ISupplyCategoryRepository;
import com.suresell.mscoreapp.domain.port.out.ISupplyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GetSuppliesByCategoryUseCase {

    private final ISupplyRepository supplyRepository;
    private final ISupplyCategoryRepository supplyCategoryRepository;

    public GetSuppliesByCategoryUseCase(ISupplyRepository supplyRepository, ISupplyCategoryRepository supplyCategoryRepository) {
        this.supplyRepository = supplyRepository;
        this.supplyCategoryRepository = supplyCategoryRepository;
    }

    public List<Supply> execute(Long supplyCategoryId) {
        Optional<SupplyCategory> supplyCategory = supplyCategoryRepository.findById(supplyCategoryId);
        if (supplyCategory.isEmpty()) {
            throw new IllegalArgumentException("SupplyCategory not found with id: " + supplyCategoryId);
        }

        String supplyCategoryName = supplyCategory.get().getName();
        return supplyRepository.findBySupplyCategoryName(supplyCategoryName);
    }
}
