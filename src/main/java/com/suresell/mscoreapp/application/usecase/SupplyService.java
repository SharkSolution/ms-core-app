package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.usecase.CreateSupplyUseCase;
import com.suresell.mscoreapp.application.usecase.GetSuppliesByCategoryUseCase;
import com.suresell.mscoreapp.domain.model.Supply;
import com.suresell.mscoreapp.application.dto.CreateSupplyDto;
import com.suresell.mscoreapp.application.dto.SupplyDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplyService {

    private final CreateSupplyUseCase createSupplyUseCase;
    private final GetSuppliesByCategoryUseCase getSuppliesBySupplyCategoryUseCase;

    public SupplyService(CreateSupplyUseCase createSupplyUseCase, GetSuppliesByCategoryUseCase getSuppliesBySupplyCategoryUseCase) {
        this.createSupplyUseCase = createSupplyUseCase;
        this.getSuppliesBySupplyCategoryUseCase = getSuppliesBySupplyCategoryUseCase;
    }

    @Transactional
    public void createSupply(CreateSupplyDto dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("Supply name cannot be empty");
        }
        if (dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Supply price must be positive");
        }
        createSupplyUseCase.execute(dto);
    }

    public List<SupplyDto> getAllSupplies() {
        return createSupplyUseCase.findAll().stream()
                .map(supply -> new SupplyDto(supply.getId(), supply.getName(), supply.getPrice(), supply.getStock(), supply.getMinStock()))
                .collect(Collectors.toList());
    }

    public List<SupplyDto> getSuppliesByCategoryId(Long supplyCategoryId) {
        List<Supply> supplies = getSuppliesBySupplyCategoryUseCase.execute(supplyCategoryId);

        return supplies.stream()
                .map(supply -> new SupplyDto(supply.getId(), supply.getName(), supply.getPrice(), supply.getStock(), supply.getMinStock()))
                .collect(Collectors.toList());
    }
}
