package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.Order;
import com.suresell.mscoreapp.domain.model.analitics.WaiterMonthlySalesRowDto;
import com.suresell.mscoreapp.domain.model.analitics.WaiterSalesRowDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WaiterSalesRepository extends JpaRepository<Order, Long> {

    @Query("SELECT new com.suresell.mscoreapp.domain.model.analitics.WaiterSalesRowDto(" +
            "o.waiterId, w.name, COALESCE(o.paymentMethod, 'UNKNOWN'), COUNT(o), COALESCE(SUM(o.total), 0)) " +
            "FROM Order o LEFT JOIN WaiterEntity w ON w.id = o.waiterId " +
            "WHERE o.createdAt BETWEEN :start AND :end " +
            "GROUP BY o.waiterId, w.name, o.paymentMethod")
    List<WaiterSalesRowDto> findSalesByWaiterBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT new com.suresell.mscoreapp.domain.model.analitics.WaiterMonthlySalesRowDto(" +
            "o.waiterId, w.name, COUNT(o), COALESCE(SUM(o.total), 0)) " +
            "FROM Order o LEFT JOIN WaiterEntity w ON w.id = o.waiterId " +
            "WHERE o.createdAt BETWEEN :start AND :end " +
            "GROUP BY o.waiterId, w.name")
    List<WaiterMonthlySalesRowDto> findMonthlySalesByWaiter(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
