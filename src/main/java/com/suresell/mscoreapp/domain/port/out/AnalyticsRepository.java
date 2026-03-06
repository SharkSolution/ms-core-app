package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.Order;
import com.suresell.mscoreapp.domain.model.analitics.CashPerformanceDto;
import com.suresell.mscoreapp.domain.model.analitics.PaymentMethodDistDto;
import com.suresell.mscoreapp.domain.model.analitics.SalesTrendDto;
import com.suresell.mscoreapp.domain.model.analitics.TopProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsRepository extends JpaRepository<Order, Long> {

    @Query("SELECT new com.suresell.mscoreapp.domain.model.analitics.SalesTrendDto(CAST(d.closingTime AS LocalDate), SUM(d.totalExpectedCash)) " +
            "FROM DailyClosureEntity d " +
            "WHERE CAST(d.closingTime AS LocalDate) BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(d.closingTime AS LocalDate) ORDER BY CAST(d.closingTime AS LocalDate) ASC")
    List<SalesTrendDto> getSalesTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.suresell.mscoreapp.domain.model.analitics.TopProductDto(p.name, SUM(i.quantity), SUM(i.totalPrice)) " +
            "FROM OrderItem i " +
            "JOIN i.order o " +
            "JOIN MenuProductEntity p ON i.productId = p.id " +
            "WHERE o.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY p.name ORDER BY SUM(i.totalPrice) DESC")
    List<TopProductDto> getTopProducts(@Param("startDateTime") LocalDateTime start, @Param("endDateTime") LocalDateTime end, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT new com.suresell.mscoreapp.domain.model.analitics.PaymentMethodDistDto(o.paymentMethod, COUNT(o), SUM(o.total)) " +
            "FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY o.paymentMethod")
    List<PaymentMethodDistDto> getPaymentMethodDistribution(@Param("startDateTime") LocalDateTime start, @Param("endDateTime") LocalDateTime end);

    @Query("SELECT new com.suresell.mscoreapp.domain.model.analitics.CashPerformanceDto(CAST(d.closingTime AS LocalDate), d.differenceAmount, d.userName) " +
            "FROM DailyClosureEntity d " +
            "WHERE CAST(d.closingTime AS LocalDate) BETWEEN :startDate AND :endDate " +
            "ORDER BY d.closingTime DESC")
    List<CashPerformanceDto> getCashPerformance(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT EXTRACT(HOUR FROM created_at) as hora, COUNT(id_order) as cantidad " +
            "FROM orders " +
            "WHERE created_at BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY EXTRACT(HOUR FROM created_at) " +
            "ORDER BY hora ASC",
            nativeQuery = true)
    List<Object[]> getPeakHoursRaw(@Param("startDateTime") LocalDateTime start, @Param("endDateTime") LocalDateTime end);
}