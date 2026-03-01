package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.SupplyCategory;

import java.util.List;
import java.util.Optional;

public interface ISupplyCategoryRepository {
    void save(SupplyCategory supplyCategory);
    Optional<SupplyCategory> findById(Long id);
    List<SupplyCategory> findAll();
}

