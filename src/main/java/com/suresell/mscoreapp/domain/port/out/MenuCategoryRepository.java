package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.MenuCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MenuCategoryRepository {
    Page<MenuCategoryEntity> findAll(Pageable pageable);
    Optional<MenuCategoryEntity> findById(String id);
    MenuCategoryEntity save(MenuCategoryEntity category);
    void deleteById(String id);
    boolean existsById(String id);
}
