package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.domain.model.SupplyCategory;
import com.suresell.mscoreapp.domain.port.out.ISupplyCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllSupplyCategoriesUseCase {

    private final ISupplyCategoryRepository supplyCategoryRepository;

    public GetAllSupplyCategoriesUseCase(ISupplyCategoryRepository supplyCategoryRepository) {
        this.supplyCategoryRepository = supplyCategoryRepository;
    }

    public List<SupplyCategory> execute() {
        return supplyCategoryRepository.findAll();
    }
}
