package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Conteo semanal de existencias registrado desde la tablet de cocina (4.2).
 * Cada fila es la cantidad contada de un insumo para una semana dada. Al registrarlo
 * se sincroniza con el inventario del Admin (1.7) fijando el stock actual del Supply.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "weekly_inventory_counts")
public class WeeklyInventoryCount extends EntidadDeNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supply_id", nullable = false)
    private Long supplyId;

    // Nombre del insumo al momento del conteo (snapshot, para listar sin joins).
    @Column(name = "supply_name")
    private String supplyName;

    @Column(name = "counted_quantity", nullable = false, precision = 12, scale = 2)
    private BigDecimal countedQuantity;

    // Stock que tenía el insumo justo antes del conteo (trazabilidad/auditoría).
    @Column(name = "previous_stock", precision = 12, scale = 2)
    private BigDecimal previousStock;

    // Diferencia = countedQuantity - previousStock (negativo = merma/pérdida).
    @Column(name = "difference", precision = 12, scale = 2)
    private BigDecimal difference;

    // Lunes de la semana contada.
    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Column(name = "source")
    private String source;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.source == null) {
            this.source = "KITCHEN";
        }
    }
}
