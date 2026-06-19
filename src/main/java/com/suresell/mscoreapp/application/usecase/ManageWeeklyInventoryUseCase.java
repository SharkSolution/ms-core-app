package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.CreateWeeklyInventoryRequest;
import com.suresell.mscoreapp.application.dto.WeeklyInventoryCountDto;
import com.suresell.mscoreapp.application.dto.WeeklyInventoryItemRequest;
import com.suresell.mscoreapp.domain.model.Supply;
import com.suresell.mscoreapp.domain.model.WeeklyInventoryCount;
import com.suresell.mscoreapp.domain.port.out.ISupplyRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.WeeklyInventoryCountJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Registro semanal de existencias desde cocina (4.2) y consulta desde el Admin (1.7).
 * Al registrar un conteo, fija el stock actual del insumo (recuento físico) para que el
 * inventario del Admin quede sincronizado con lo contado en cocina.
 */
@Service
public class ManageWeeklyInventoryUseCase {

    private static final String DEFAULT_SOURCE = "KITCHEN";

    private final WeeklyInventoryCountJpaRepository repository;
    private final ISupplyRepository supplyRepository;

    public ManageWeeklyInventoryUseCase(WeeklyInventoryCountJpaRepository repository,
                                        ISupplyRepository supplyRepository) {
        this.repository = repository;
        this.supplyRepository = supplyRepository;
    }

    @Transactional
    public List<WeeklyInventoryCountDto> register(CreateWeeklyInventoryRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un insumo en el conteo");
        }

        LocalDate weekStart = request.getWeekStart() != null
                ? request.getWeekStart()
                : LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        String source = (request.getSource() != null && !request.getSource().isBlank())
                ? request.getSource()
                : DEFAULT_SOURCE;

        List<WeeklyInventoryCountDto> result = new ArrayList<>();
        for (WeeklyInventoryItemRequest item : request.getItems()) {
            if (item.getSupplyId() == null || item.getCountedQuantity() == null) {
                continue;
            }
            Supply supply = supplyRepository.findById(item.getSupplyId())
                    .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado: " + item.getSupplyId()));

            // Trazabilidad: capturamos el stock previo y la diferencia antes de fijar el nuevo.
            BigDecimal previousStock = supply.getStock() != null ? supply.getStock() : BigDecimal.ZERO;
            BigDecimal counted = item.getCountedQuantity();
            BigDecimal difference = counted.subtract(previousStock);

            // Sincronización con el inventario del Admin: el conteo físico fija el stock actual.
            supplyRepository.setStock(supply.getId(), counted);

            WeeklyInventoryCount count = new WeeklyInventoryCount();
            count.setSupplyId(supply.getId());
            count.setSupplyName(supply.getName());
            count.setCountedQuantity(counted);
            count.setPreviousStock(previousStock);
            count.setDifference(difference);
            count.setWeekStart(weekStart);
            count.setSource(source);
            count.setCreatedBy(request.getCreatedBy());

            result.add(toDto(repository.save(count)));
        }
        return result;
    }

    public List<WeeklyInventoryCountDto> list(LocalDate weekStart) {
        List<WeeklyInventoryCount> rows = (weekStart != null)
                ? repository.findByWeekStartOrderByCreatedAtDesc(weekStart)
                : repository.findAllByOrderByCreatedAtDesc();
        return rows.stream().map(this::toDto).collect(Collectors.toList());
    }

    private WeeklyInventoryCountDto toDto(WeeklyInventoryCount c) {
        return new WeeklyInventoryCountDto(
                c.getId(),
                c.getSupplyId(),
                c.getSupplyName(),
                c.getCountedQuantity(),
                c.getPreviousStock(),
                c.getDifference(),
                c.getWeekStart(),
                c.getSource(),
                c.getCreatedBy(),
                c.getCreatedAt()
        );
    }
}
