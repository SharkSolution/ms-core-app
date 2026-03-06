package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.SupplyCategory;
import com.suresell.mscoreapp.domain.port.out.ISupplyCategoryRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.SupplyCategoryJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SupplyCategoryRepositoryImpl implements ISupplyCategoryRepository {

    private final SupplyCategoryJpaRepository supplyCategoryJpaRepository;

    public SupplyCategoryRepositoryImpl(SupplyCategoryJpaRepository supplyCategoryJpaRepository) {
        this.supplyCategoryJpaRepository = supplyCategoryJpaRepository;
    }

    @Override
    public void save(SupplyCategory supplyCategory) {
        supplyCategoryJpaRepository.save(supplyCategory);
    }

    @Override
    public Optional<SupplyCategory> findById(Long id) {
        return supplyCategoryJpaRepository.findById(id);
    }

    @Override
    public List<SupplyCategory> findAll() {
        return supplyCategoryJpaRepository.findAll();
    }
}
