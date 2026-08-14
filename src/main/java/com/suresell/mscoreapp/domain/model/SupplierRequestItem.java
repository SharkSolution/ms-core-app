package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Línea de una solicitud de pedido. supplyId enlaza (opcional) al maestro Supply. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "supplier_request_items")
public class SupplierRequestItem extends EntidadDeNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private SupplierRequest request;

    @Column(name = "supply_id")
    private Long supplyId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(length = 40)
    private String unit;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal quantity;
}
