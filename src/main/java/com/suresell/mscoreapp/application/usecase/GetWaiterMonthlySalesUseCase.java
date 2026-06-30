package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.WaiterMonthlySalesItem;
import com.suresell.mscoreapp.application.dto.WaiterMonthlySalesResponse;
import com.suresell.mscoreapp.domain.model.analitics.WaiterMonthlySalesRowDto;
import com.suresell.mscoreapp.domain.port.out.WaiterSalesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetWaiterMonthlySalesUseCase {

    private static final String UNASSIGNED_LABEL = "Sin asignar";

    private final WaiterSalesRepository repository;

    public GetWaiterMonthlySalesUseCase(WaiterSalesRepository repository) {
        this.repository = repository;
    }

    public WaiterMonthlySalesResponse execute(int year, int month) {
        YearMonth period = YearMonth.of(year, month);
        LocalDateTime start = period.atDay(1).atStartOfDay();
        LocalDateTime end = period.atEndOfMonth().atTime(LocalTime.MAX);

        List<WaiterMonthlySalesRowDto> rows = repository.findMonthlySalesByWaiter(start, end);

        List<WaiterMonthlySalesItem> waiters = new ArrayList<>();
        WaiterMonthlySalesItem unassigned = null;
        BigDecimal grandTotal = BigDecimal.ZERO;
        long totalOrders = 0L;

        for (WaiterMonthlySalesRowDto row : rows) {
            long count = row.ordersCount() != null ? row.ordersCount() : 0L;
            BigDecimal total = row.total() != null ? row.total() : BigDecimal.ZERO;

            grandTotal = grandTotal.add(total);
            totalOrders += count;

            if (row.waiterId() == null) {
                unassigned = new WaiterMonthlySalesItem(null, UNASSIGNED_LABEL, count, total);
            } else {
                String name = row.waiterName() != null ? row.waiterName() : UNASSIGNED_LABEL;
                waiters.add(new WaiterMonthlySalesItem(row.waiterId(), name, count, total));
            }
        }

        waiters.sort(Comparator.comparing(WaiterMonthlySalesItem::total).reversed());

        return new WaiterMonthlySalesResponse(year, month, grandTotal, totalOrders, waiters, unassigned);
    }
}
