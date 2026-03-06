package com.suresell.mscoreapp.domain.model;

import com.suresell.mscoreapp.shared.enums.ClosureStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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

    @Column(name = "user_name")
    private String userName;

    @Column(name = "opening_time")
    private LocalDateTime openingTime;

    @Column(name = "closing_time")
    private LocalDateTime closingTime;

    @Column(name = "total_expected_cash", precision = 38, scale = 2)
    private BigDecimal totalExpectedCash;

    @Column(name = "total_expected_card", precision = 38, scale = 2)
    private BigDecimal totalExpectedCard;

    @Column(name = "total_expected_nequi", precision = 38, scale = 2)
    private BigDecimal totalExpectedNequi;

    @Column(name = "total_expected_qr", precision = 38, scale = 2)
    private BigDecimal totalExpectedQr;

    @Column(name = "total_counted_cash", precision = 38, scale = 2)
    private BigDecimal totalCountedCash;

    @Column(name = "total_counted_card", precision = 38, scale = 2)
    private BigDecimal totalCountedCard;

    @Column(name = "total_counted_nequi", precision = 38, scale = 2)
    private BigDecimal totalCountedNequi;

    @Column(name = "total_counted_qr", precision = 38, scale = 2)
    private BigDecimal totalCountedQr;

    @Column(name = "difference_amount", precision = 38, scale = 2)
    private BigDecimal differenceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClosureStatus status;

    @Column(name = "notes")
    private String notes;

    @Column(name = "base_balance_for_next_day", precision = 38, scale = 2)
    private BigDecimal baseBalanceForNextDay;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
