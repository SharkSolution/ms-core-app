package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.MenuProductEntity;
import com.suresell.mscoreapp.domain.port.out.MenuProductRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.MenuProductJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MenuProductRepositoryImpl implements MenuProductRepository {

    private final MenuProductJpaRepository jpaRepository;

    public MenuProductRepositoryImpl(MenuProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<MenuProductEntity> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Optional<MenuProductEntity> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<MenuProductEntity> findByCategoryId(String categoryId, Pageable pageable) {
        return jpaRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    public MenuProductEntity save(MenuProductEntity product) {
        return jpaRepository.save(product);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }
}
