package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.SupplierRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRequestJpaRepository extends JpaRepository<SupplierRequest, Long> {
    List<SupplierRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<SupplierRequest> findAllByOrderByCreatedAtDesc();
}
