package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyJpaRepository extends JpaRepository<Supply, Long> {

    List<Supply> findBySupplyCategoryName(String supplyCategoryName);
}
