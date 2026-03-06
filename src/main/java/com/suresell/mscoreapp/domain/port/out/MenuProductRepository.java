package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.MenuProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MenuProductRepository {
    Page<MenuProductEntity> findAll(Pageable pageable);
    Optional<MenuProductEntity> findById(String id);
    Page<MenuProductEntity> findByCategoryId(String categoryId, Pageable pageable);
    MenuProductEntity save(MenuProductEntity product);
    void deleteById(String id);
    boolean existsById(String id);
}
