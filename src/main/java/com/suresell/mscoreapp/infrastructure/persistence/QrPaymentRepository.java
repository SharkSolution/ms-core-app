package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.QrPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface QrPaymentRepository extends JpaRepository<QrPaymentEntity, Long> {
    Optional<QrPaymentEntity> findByPaymentDate(LocalDate paymentDate);
}
