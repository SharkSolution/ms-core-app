package com.suresell.mscoreapp.domain.model;

import com.suresell.mscoreapp.shared.enums.Reason;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
// tenant_id no entra en equals: todas las filas de una sesion comparten negocio.
@EqualsAndHashCode(callSuper = false)
@Table(name = "supply_consumptions")
@AllArgsConstructor
@NoArgsConstructor
public class SupplyConsumptionEntity extends EntidadDeNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supply_id", nullable = false)
    private Long supplyId;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Reason reason;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;
}
