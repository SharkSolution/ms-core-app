package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.CreateSupplyDto;
import com.suresell.mscoreapp.application.dto.SupplyDto;
import com.suresell.mscoreapp.domain.model.Supply;
import com.suresell.mscoreapp.domain.model.SupplyCategory;
import com.suresell.mscoreapp.domain.port.out.ISupplyCategoryRepository;
import com.suresell.mscoreapp.domain.port.out.ISupplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplyService {

    private final CreateSupplyUseCase createSupplyUseCase;
    private final GetSuppliesByCategoryUseCase getSuppliesBySupplyCategoryUseCase;
    private final ISupplyRepository supplyRepository;
    private final ISupplyCategoryRepository supplyCategoryRepository;

    public SupplyService(CreateSupplyUseCase createSupplyUseCase,
                         GetSuppliesByCategoryUseCase getSuppliesBySupplyCategoryUseCase,
                         ISupplyRepository supplyRepository,
                         ISupplyCategoryRepository supplyCategoryRepository) {
        this.createSupplyUseCase = createSupplyUseCase;
        this.getSuppliesBySupplyCategoryUseCase = getSuppliesBySupplyCategoryUseCase;
        this.supplyRepository = supplyRepository;
        this.supplyCategoryRepository = supplyCategoryRepository;
    }

    @Transactional
    public void updateSupply(Long id, CreateSupplyDto dto) {
        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado"));

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            supply.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            supply.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            supply.setStock(dto.getStock());
        }
        if (dto.getMinStock() != null) {
            supply.setMinStock(dto.getMinStock());
        }
        if (dto.getSupplyCategoryId() != null) {
            SupplyCategory category = supplyCategoryRepository.findById(dto.getSupplyCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("SupplyCategory not found"));
            supply.setSupplyCategory(category);
        }
        supplyRepository.save(supply);
    }

    @Transactional
    public void deleteSupply(Long id) {
        supplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado"));
        supplyRepository.deleteById(id);
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
                .map(supply -> new SupplyDto(
                        supply.getId(), 
                        supply.getName(), 
                        supply.getPrice(), 
                        supply.getStock(), 
                        supply.getMinStock(),
                        supply.getSupplyCategory() != null ? supply.getSupplyCategory().getName() : null
                ))
                .collect(Collectors.toList());
    }

    public List<SupplyDto> getSuppliesByCategoryId(Long supplyCategoryId) {
        List<Supply> supplies = getSuppliesBySupplyCategoryUseCase.execute(supplyCategoryId);

        return supplies.stream()
                .map(supply -> new SupplyDto(
                        supply.getId(), 
                        supply.getName(), 
                        supply.getPrice(), 
                        supply.getStock(), 
                        supply.getMinStock(),
                        supply.getSupplyCategory() != null ? supply.getSupplyCategory().getName() : null
                ))
                .collect(Collectors.toList());
    }
}
