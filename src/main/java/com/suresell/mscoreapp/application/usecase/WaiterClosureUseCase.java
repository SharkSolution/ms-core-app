package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.WaiterClosurePreviewResponse;
import com.suresell.mscoreapp.application.dto.WaiterClosureRequest;
import com.suresell.mscoreapp.application.dto.WaiterClosureResponse;
import com.suresell.mscoreapp.domain.model.OrderStatus;
import com.suresell.mscoreapp.domain.model.WaiterClosure;
import com.suresell.mscoreapp.domain.port.out.OrderRepository;
import com.suresell.mscoreapp.domain.port.out.WaiterClosureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class WaiterClosureUseCase {

    private static final ZoneId BOGOTA_ZONE = ZoneId.of("America/Bogota");
    private final WaiterClosureRepository waiterClosureRepository;
    private final OrderRepository orderRepository;

    public WaiterClosureUseCase(WaiterClosureRepository waiterClosureRepository,
                                 OrderRepository orderRepository) {
        this.waiterClosureRepository = waiterClosureRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public WaiterClosurePreviewResponse getWaiterClosurePreview(String waiterId) {
        LocalDateTime startOfDay = LocalDate.now(BOGOTA_ZONE).atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now(BOGOTA_ZONE).with(LocalTime.MAX);

        List<Object[]> results = orderRepository.sumTotalsByPaymentMethodAndWaiter(
                waiterId,
                OrderStatus.pagado,
                startOfDay,
                endOfDay
        );

        BigDecimal totalExpectedCash = BigDecimal.ZERO;
        BigDecimal totalExpectedCard = BigDecimal.ZERO;
        BigDecimal totalExpectedQr = BigDecimal.ZERO;

        for (Object[] result : results) {
            String method = (String) result[0];
            BigDecimal sum = result[1] == null ? BigDecimal.ZERO : (BigDecimal) result[1];
            if (method == null) continue;

            String normMethod = method.toUpperCase();
            if (normMethod.equals("CASH") || normMethod.equals("EFECTIVO")) {
                totalExpectedCash = totalExpectedCash.add(sum);
            } else if (normMethod.equals("CARD") || normMethod.equals("TARJETA")) {
                totalExpectedCard = totalExpectedCard.add(sum);
            } else if (normMethod.equals("QR") || normMethod.equals("NEQUI")) {
                totalExpectedQr = totalExpectedQr.add(sum);
            }
        }

        BigDecimal totalExpected = totalExpectedCash.add(totalExpectedCard).add(totalExpectedQr);
        BigDecimal lastBaseCash = waiterClosureRepository.findTopByWaiterIdOrderByClosedAtDesc(waiterId)
                .map(WaiterClosure::getBaseCash)
                .orElse(BigDecimal.ZERO);

        return WaiterClosurePreviewResponse.builder()
                .waiterId(waiterId)
                .previewTime(LocalDateTime.now(BOGOTA_ZONE))
                .totalExpectedCash(totalExpectedCash)
                .totalExpectedCard(totalExpectedCard)
                .totalExpectedQr(totalExpectedQr)
                .totalExpected(totalExpected)
                .lastBaseCash(lastBaseCash)
                .message("Vista previa generada para el mesero " + waiterId)
                .build();
    }

    @Transactional
    public WaiterClosureResponse executeWaiterClosure(WaiterClosureRequest request) {
        LocalDateTime now = LocalDateTime.now(BOGOTA_ZONE);
        LocalDate today = LocalDate.now(BOGOTA_ZONE);

        WaiterClosurePreviewResponse preview = getWaiterClosurePreview(request.getWaiterId());

        BigDecimal expectedCash = preview.getTotalExpectedCash();
        BigDecimal expectedCard = preview.getTotalExpectedCard();
        BigDecimal expectedQr = preview.getTotalExpectedQr();

        BigDecimal baseCash = request.getBaseCash();
        BigDecimal expectedCashWithBase = expectedCash.add(baseCash);

        BigDecimal differenceCash = request.getTotalCountedCash().subtract(expectedCashWithBase);
        BigDecimal differenceCard = request.getTotalCountedCard().subtract(expectedCard);
        BigDecimal differenceQr = request.getTotalCountedQr().subtract(expectedQr);
        BigDecimal totalDifference = differenceCash.add(differenceCard).add(differenceQr);

        String status = determineStatus(totalDifference);
        String message = generateClosureMessage(status, totalDifference);

        WaiterClosure closure = new WaiterClosure();
        closure.setId(UUID.randomUUID());
        closure.setWaiterId(request.getWaiterId());
        closure.setWaiterName(request.getWaiterName());
        closure.setClosureDate(today);
        closure.setBaseCash(baseCash);

        closure.setTotalExpectedCash(expectedCash);
        closure.setTotalExpectedCard(expectedCard);
        closure.setTotalExpectedQr(expectedQr);

        closure.setTotalCountedCash(request.getTotalCountedCash());
        closure.setTotalCountedCard(request.getTotalCountedCard());
        closure.setTotalCountedQr(request.getTotalCountedQr());

        closure.setDifferenceCash(differenceCash);
        closure.setDifferenceCard(differenceCard);
        closure.setDifferenceQr(differenceQr);
        closure.setTotalDifference(totalDifference);

        closure.setStatus(status);
        closure.setNotes(request.getNotes());
        closure.setClosedAt(now);

        WaiterClosure saved = waiterClosureRepository.save(closure);

        return WaiterClosureResponse.builder()
                .id(saved.getId())
                .waiterId(saved.getWaiterId())
                .waiterName(saved.getWaiterName())
                .closedAt(saved.getClosedAt())
                .baseCash(saved.getBaseCash())
                .totalExpectedCash(saved.getTotalExpectedCash())
                .totalExpectedCard(saved.getTotalExpectedCard())
                .totalExpectedQr(saved.getTotalExpectedQr())
                .totalCountedCash(saved.getTotalCountedCash())
                .totalCountedCard(saved.getTotalCountedCard())
                .totalCountedQr(saved.getTotalCountedQr())
                .differenceCash(saved.getDifferenceCash())
                .differenceCard(saved.getDifferenceCard())
                .differenceQr(saved.getDifferenceQr())
                .totalDifference(saved.getTotalDifference())
                .status(saved.getStatus())
                .notes(saved.getNotes())
                .message(message)
                .build();
    }

    private String determineStatus(BigDecimal difference) {
        int comparison = difference.compareTo(BigDecimal.ZERO);
        if (comparison == 0) return "BALANCED";
        if (comparison > 0) return "POSITIVE_DIFF";
        return "NEGATIVE_DIFF";
    }

    private String generateClosureMessage(String status, BigDecimal difference) {
        return switch (status) {
            case "BALANCED" -> "✅ Cierre de turno cuadrado sin diferencias.";
            case "POSITIVE_DIFF" -> String.format("⚠️ Sobrante de caja: $%.2f", difference);
            case "NEGATIVE_DIFF" -> String.format("❌ Faltante de caja: $%.2f", difference.abs());
            default -> "Cierre de turno completado.";
        };
    }
}
