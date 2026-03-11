package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.domain.model.analitics.*;
import com.suresell.mscoreapp.domain.port.out.AnalyticsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardAnalyticsUseCase {

    private final AnalyticsRepository analyticsRepository;

    public DashboardAnalyticsUseCase(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<SalesTrendDto> getSalesTrend(LocalDate startDate, LocalDate endDate) {
        return analyticsRepository.getSalesTrend(startDate, endDate);
    }

    public List<TopProductDto> getTopProducts(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        // PageRequest aplica el LIMIT a nivel de Base de Datos
        return analyticsRepository.getTopProducts(start, end, PageRequest.of(0, limit));
    }

    public List<PaymentMethodDistDto> getPaymentMethods(LocalDate startDate, LocalDate endDate) {
        return analyticsRepository.getPaymentMethodDistribution(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    public List<CashPerformanceDto> getCashPerformance(LocalDate startDate, LocalDate endDate) {
        return analyticsRepository.getCashPerformance(startDate, endDate);
    }

        public List<PeakHourDto> getPeakHours(LocalDate startDate, LocalDate endDate) {
            List<Object[]> results = analyticsRepository.getPeakHoursRaw(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
            return results.stream()
                    .map(row -> new PeakHourDto(
                            ((Number) row[0]).intValue(),
                            ((Number) row[1]).longValue()
                    ))
                    .collect(Collectors.toList());
        }
    
        public DashboardResponse getDashboardMetrics() {
            LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
    
            BigDecimal todaySales = analyticsRepository.getTotalSalesToday(start, end);
            Long todayOrders = analyticsRepository.getTotalOrdersToday(start, end);
            Long inventoryConsumption = analyticsRepository.getInventoryConsumptionToday(start, end);
    
            BigDecimal averageTicket = (todayOrders > 0)
                    ? todaySales.divide(BigDecimal.valueOf(todayOrders), 2, BigDecimal.ROUND_HALF_UP)
                    : BigDecimal.ZERO;
    
            List<TopProductDto> topProducts = analyticsRepository.getTopProducts(start, end, PageRequest.of(0, 5));
            List<PaymentMethodDistDto> paymentMethods = analyticsRepository.getPaymentMethodDistribution(start, end);
    
            return new DashboardResponse(
                    todaySales,
                    todayOrders,
                    averageTicket,
                    inventoryConsumption,
                    topProducts,
                    paymentMethods
            );
        }
    }
    