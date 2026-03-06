package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.MenuCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuCategoryJpaRepository extends JpaRepository<MenuCategoryEntity, String> {
}
