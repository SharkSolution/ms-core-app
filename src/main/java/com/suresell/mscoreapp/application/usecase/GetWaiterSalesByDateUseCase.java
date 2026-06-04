package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.WaiterSalesItemDto;
import com.suresell.mscoreapp.application.dto.WaiterSalesResponse;
import com.suresell.mscoreapp.domain.model.analitics.WaiterSalesRowDto;
import com.suresell.mscoreapp.domain.port.out.WaiterSalesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class GetWaiterSalesByDateUseCase {

    private static final String UNASSIGNED_LABEL = "Sin asignar";

    private final WaiterSalesRepository repository;

    public GetWaiterSalesByDateUseCase(WaiterSalesRepository repository) {
        this.repository = repository;
    }

    public WaiterSalesResponse execute(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<WaiterSalesRowDto> rows = repository.findSalesByWaiterBetween(start, end);

        Map<Long, Aggregate> byWaiter = new HashMap<>();
        Aggregate unassigned = new Aggregate(null, UNASSIGNED_LABEL);
        Map<String, BigDecimal> grandByMethod = new HashMap<>();
        BigDecimal grandTotal = BigDecimal.ZERO;
        long totalOrders = 0L;

        for (WaiterSalesRowDto row : rows) {
            Aggregate target = row.waiterId() == null
                    ? unassigned
                    : byWaiter.computeIfAbsent(row.waiterId(), id -> new Aggregate(id, row.waiterName()));

            target.add(row.paymentMethod(), row.total(), row.ordersCount());

            grandByMethod.merge(row.paymentMethod(), row.total(), BigDecimal::add);
            grandTotal = grandTotal.add(row.total());
            totalOrders += row.ordersCount();
        }

        List<WaiterSalesItemDto> waiters = byWaiter.values().stream()
                .map(Aggregate::toDto)
                .sorted(Comparator.comparing(WaiterSalesItemDto::total).reversed())
                .toList();

        WaiterSalesItemDto unassignedDto = unassigned.isEmpty() ? null : unassigned.toDto();

        return new WaiterSalesResponse(date, grandTotal, totalOrders, grandByMethod, waiters, unassignedDto);
    }

    private static final class Aggregate {
        private final Long waiterId;
        private final String waiterName;
        private final Map<String, BigDecimal> breakdown = new HashMap<>();
        private BigDecimal total = BigDecimal.ZERO;
        private long ordersCount = 0L;

        Aggregate(Long waiterId, String waiterName) {
            this.waiterId = waiterId;
            this.waiterName = waiterName != null ? waiterName : UNASSIGNED_LABEL;
        }

        void add(String method, BigDecimal amount, long count) {
            breakdown.merge(method, amount, BigDecimal::add);
            total = total.add(amount);
            ordersCount += count;
        }

        boolean isEmpty() {
            return ordersCount == 0L;
        }

        WaiterSalesItemDto toDto() {
            return new WaiterSalesItemDto(waiterId, waiterName, ordersCount, total, Map.copyOf(breakdown));
        }
    }
}
