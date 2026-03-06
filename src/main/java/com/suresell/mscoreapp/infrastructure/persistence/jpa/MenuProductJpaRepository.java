package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.MenuProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuProductJpaRepository extends JpaRepository<MenuProductEntity, String> {
    Page<MenuProductEntity> findByCategoryId(String categoryId, Pageable pageable);
}
