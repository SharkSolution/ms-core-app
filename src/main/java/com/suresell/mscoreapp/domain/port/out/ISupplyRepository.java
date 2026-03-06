package com.suresell.mscoreapp.domain.port.out;


import com.suresell.mscoreapp.domain.model.Supply;

import java.math.BigDecimal;
import java.util.List;

public interface ISupplyRepository {
    void save(Supply supply);
    List<Supply> findAll();
    List<Supply> findBySupplyCategoryName(String supplyCategoryName);
    void updateStock(Long supplyId, BigDecimal quantity);
}