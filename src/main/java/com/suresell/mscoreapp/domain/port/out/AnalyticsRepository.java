package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.Order;
import com.suresell.mscoreapp.domain.model.analitics.CashPerformanceDto;
import com.suresell.mscoreapp.domain.model.analitics.PaymentMethodDistDto;
import com.suresell.mscoreapp.domain.model.analitics.SalesTrendDto;
import com.suresell.mscoreapp.domain.model.analitics.TopProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsRepository extends JpaRepository<Order, Long> {

    @Query("SELECT new com.suresell.mscoreapp.domain.model.analitics.SalesTrendDto(d.closureDate, SUM(d.totalExpected)) " +
            "FROM DailyClosureEntity d " +
            "WHERE d.closureDate BETWEEN :startDate AND :endDate " +
            "GROUP BY d.closureDate ORDER BY d.closureDate ASC")
    List<SalesTrendDto> getSalesTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.suresell.mscoreapp.domain.model.analitics.TopProductDto(p.name, SUM(i.quantity), SUM(i.totalPrice)) " +
            "FROM OrderItem i " +
            "JOIN i.order o " +
            "JOIN MenuProductEntity p ON i.productId = p.id " +
            "WHERE o.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY p.name ORDER BY SUM(i.totalPrice) DESC")
    List<TopProductDto> getTopProducts(@Param("startDateTime") LocalDateTime start, @Param("endDateTime") LocalDateTime end, org.springframework.data.domain.Pageable pageable);

    /**
     * Reparto de ingresos por medio de pago, <b>abriendo los pagos mixtos</b>.
     *
     * <p>Antes agrupaba por {@code orders.payment_method}, así que una orden
     * pagada en parte con efectivo y en parte con QR caía entera en un bloque
     * "MIXED": ni el efectivo sumaba a efectivo, ni el QR a QR. El total general
     * cuadraba, pero el reparto —que es justo para lo que sirve esta pantalla—
     * estaba mal.
     *
     * <p>Ahora cada porción cuenta como <b>una transacción de su medio</b>: si
     * algo se cobró por QR, el contador de QR sube en 1, porque efectivamente
     * entró un QR más que hay que conciliar contra el extracto del banco.
     *
     * <p>El {@code LEFT JOIN} con {@code COALESCE} cubre los dos casos sin
     * ramificar: una orden simple no tiene filas en {@code order_payments}, así
     * que cae a su propio método y total; una mixta produce una fila por
     * porción. La plata no se duplica ni se pierde.
     *
     * <p>Es consulta nativa porque {@code order_payments} no está mapeado como
     * entidad en este servicio.
     */
    @Query(value = "SELECT metodo AS method, count(*) AS trxCount, sum(monto) AS totalMoney FROM ("
            + "  SELECT COALESCE(p.method, o.payment_method) AS metodo,"
            + "         COALESCE(p.amount, o.total)          AS monto"
            + "  FROM orders o"
            + "  LEFT JOIN order_payments p ON p.order_uuid_id = o.uuid_id"
            + "  WHERE o.created_at BETWEEN :startDateTime AND :endDateTime"
            + "    AND o.deleted_at IS NULL"
            + ") x GROUP BY metodo ORDER BY sum(monto) DESC", nativeQuery = true)
    List<PaymentMethodDistProjection> getPaymentMethodDistribution(
            @Param("startDateTime") LocalDateTime start, @Param("endDateTime") LocalDateTime end);

    /** Proyección de la consulta nativa; se traduce al DTO en el caso de uso. */
    interface PaymentMethodDistProjection {
        String getMethod();
        Long getTrxCount();
        BigDecimal getTotalMoney();
    }

    @Query("SELECT new com.suresell.mscoreapp.domain.model.analitics.CashPerformanceDto(d.closureDate, d.totalDifference, d.userName) " +
            "FROM DailyClosureEntity d " +
            "WHERE d.closureDate BETWEEN :startDate AND :endDate " +
            "ORDER BY d.closureDate DESC")
    List<CashPerformanceDto> getCashPerformance(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(value = "SELECT EXTRACT(HOUR FROM created_at) as hora, COUNT(id_order) as cantidad " +
                "FROM orders " +
                "WHERE created_at BETWEEN :startDateTime AND :endDateTime " +
                "GROUP BY EXTRACT(HOUR FROM created_at) " +
                "ORDER BY hora ASC",
                nativeQuery = true)
        List<Object[]> getPeakHoursRaw(@Param("startDateTime") LocalDateTime start, @Param("endDateTime") LocalDateTime end);
    
        @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
        BigDecimal getTotalSalesToday(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
        @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
        Long getTotalOrdersToday(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
        @Query("SELECT COUNT(sc) FROM SupplyConsumptionEntity sc WHERE sc.registrationDate BETWEEN :start AND :end")
        Long getInventoryConsumptionToday(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    }
    