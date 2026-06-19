package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.Supply;
import com.suresell.mscoreapp.domain.port.out.ISupplyRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.SupplyJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class SupplyRepositoryImpl implements ISupplyRepository {

    private final SupplyJpaRepository supplyJpaRepository;

    public SupplyRepositoryImpl(SupplyJpaRepository supplyJpaRepository) {
        this.supplyJpaRepository = supplyJpaRepository;
    }

    @Override
    public void save(Supply supply) {
        supplyJpaRepository.save(supply);
    }

    @Override
    public List<Supply> findAll() {
        return supplyJpaRepository.findAll();
    }

    @Override
    public List<Supply> findBySupplyCategoryName(String supplyCategoryName) {
        return supplyJpaRepository.findBySupplyCategoryName(supplyCategoryName);
    }

    @Override
    @Transactional
    public void updateStock(Long supplyId, BigDecimal quantity) {
        supplyJpaRepository.findById(supplyId).ifPresent(supply -> {
            supply.setStock(supply.getStock().add(quantity));
            supplyJpaRepository.save(supply);
        });
    }

    @Override
    public Optional<Supply> findById(Long id) {
        return supplyJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        supplyJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void setStock(Long supplyId, BigDecimal newStock) {
        supplyJpaRepository.findById(supplyId).ifPresent(supply -> {
            supply.setStock(newStock);
            supplyJpaRepository.save(supply);
        });
    }
}
