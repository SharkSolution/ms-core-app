package com.suresell.mscoreapp.domain.model;

import com.suresell.mscoreapp.shared.enums.ClosureStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_closure")
public class DailyClosureEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "opening_time", nullable = false)
    private LocalDateTime openingTime;

    @Column(name = "closing_time")
    private LocalDateTime closingTime;

    @Column(name = "closure_date")
    private LocalDate closureDate;

    // Totales Esperados detallados
    @Column(name = "total_expected_cash", precision = 38, scale = 2)
    private BigDecimal totalExpectedCash;

    @Column(name = "total_expected_card", precision = 38, scale = 2)
    private BigDecimal totalExpectedCard;

    @Column(name = "total_expected_nequi", precision = 38, scale = 2)
    private BigDecimal totalExpectedNequi;

    @Column(name = "total_expected_qr", precision = 38, scale = 2)
    private BigDecimal totalExpectedQr;

    // Totales Contados detallados
    @Column(name = "total_counted_cash", precision = 38, scale = 2)
    private BigDecimal totalCountedCash;

    @Column(name = "total_counted_card", precision = 38, scale = 2)
    private BigDecimal totalCountedCard;

    @Column(name = "total_counted_nequi", precision = 38, scale = 2)
    private BigDecimal totalCountedNequi;

    @Column(name = "total_counted_qr", precision = 38, scale = 2)
    private BigDecimal totalCountedQr;

    // Totales Consolidados para Analíticas
    @Column(name = "total_expected", precision = 38, scale = 2)
    private BigDecimal totalExpected;

    @Column(name = "total_counted", precision = 38, scale = 2)
    private BigDecimal totalCounted;

    @Column(name = "difference_amount", precision = 38, scale = 2)
    private BigDecimal differenceAmount;

    @Column(name = "total_difference", precision = 38, scale = 2)
    private BigDecimal totalDifference; // Aliado para compatibilidad con analíticas

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClosureStatus status;

    @Column(name = "status_message", columnDefinition = "TEXT")
    private String statusMessage;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "base_balance_for_next_day", precision = 38, scale = 2)
    private BigDecimal baseBalanceForNextDay;

    @Column(name = "cash_count_audit", columnDefinition = "TEXT")
    private String cashCountAudit;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.closureDate == null && this.closingTime != null) {
            this.closureDate = this.closingTime.toLocalDate();
        }
    }
}
