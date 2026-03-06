package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.MenuCategoryEntity;
import com.suresell.mscoreapp.domain.port.out.MenuCategoryRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.MenuCategoryJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MenuCategoryRepositoryImpl implements MenuCategoryRepository {

    private final MenuCategoryJpaRepository jpaRepository;

    public MenuCategoryRepositoryImpl(MenuCategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<MenuCategoryEntity> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Optional<MenuCategoryEntity> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public MenuCategoryEntity save(MenuCategoryEntity category) {
        return jpaRepository.save(category);
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
