package com.suresell.mscoreapp.domain.port.out;


import com.suresell.mscoreapp.domain.model.Supply;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ISupplyRepository {
    void save(Supply supply);
    List<Supply> findAll();
    List<Supply> findBySupplyCategoryName(String supplyCategoryName);
    void updateStock(Long supplyId, BigDecimal quantity);
    Optional<Supply> findById(Long id);
    void deleteById(Long id);
    // Fija el stock a un valor absoluto (recuento físico), a diferencia de updateStock (delta).
    void setStock(Long supplyId, BigDecimal newStock);
}