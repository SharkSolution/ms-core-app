package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.usecase.SupplyCategoryEntityMapper;
import com.suresell.mscoreapp.application.usecase.CreateSupplyCategoryUseCase;
import com.suresell.mscoreapp.application.usecase.GetAllSupplyCategoriesUseCase;
import com.suresell.mscoreapp.application.dto.SupplyCategoryDto;
import com.suresell.mscoreapp.domain.model.SupplyCategory;
import com.suresell.mscoreapp.application.dto.CreateSupplyCategoryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupplyCategoryService {

    private final CreateSupplyCategoryUseCase createSupplyCategoryUseCase;
    private final GetAllSupplyCategoriesUseCase getAllCategoriesUseCase;
    private final SupplyCategoryEntityMapper supplyCategoryMapper;

    public SupplyCategoryService(CreateSupplyCategoryUseCase createSupplyCategoryUseCase,
                           GetAllSupplyCategoriesUseCase getAllCategoriesUseCase,
                           SupplyCategoryEntityMapper supplyCategoryMapper) {
        this.createSupplyCategoryUseCase = createSupplyCategoryUseCase;
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
        this.supplyCategoryMapper = supplyCategoryMapper;
    }

    @Transactional
    public void createSupplyCategory(CreateSupplyCategoryDto dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("SupplyCategory name cannot be empty");
        }
        createSupplyCategoryUseCase.execute(dto);
    }

    public List<SupplyCategoryDto> getAllSupplyCategories() {
        List<SupplyCategory> categories = getAllCategoriesUseCase.execute();
        return supplyCategoryMapper.toDomainList(categories);
    }
}
