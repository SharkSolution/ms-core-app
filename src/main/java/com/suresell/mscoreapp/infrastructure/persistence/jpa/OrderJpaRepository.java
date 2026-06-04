package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.suresell.mscoreapp.domain.model.OrderStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query("SELECT o.paymentMethod, SUM(o.total) FROM Order o " +
           "WHERE o.waiterId = :waiterId AND o.status = :status AND o.createdAt BETWEEN :startTime AND :endTime " +
           "GROUP BY o.paymentMethod")
    List<Object[]> sumTotalsByPaymentMethodAndWaiter(
            @Param("waiterId") String waiterId,
            @Param("status") OrderStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
