package com.braidsbeautyByAngie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factura")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacturaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_numero", nullable = false)
    private Long facturaNumero;
    @Column(name = "factura_serie", nullable = false)
    private String facturaSerie;
    @Column(name = "factura_serienumero", nullable = false)
    private String facturaSerienumero;
    @Column(name = "fecha_emision", nullable = false)
    private Timestamp fechaEmision;
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;
    @Column(name = "igv", nullable = false)
    private BigDecimal igv;
    @Column(name = "descuentos", nullable = false)
    private BigDecimal descuentos;
    @Column(name = "anticipios", nullable = false)
    private BigDecimal anticipios;
    @Column(name = "valor_venta", nullable = false)
    private BigDecimal valorVenta;
    @Column(name = "isc", nullable = true)
    private BigDecimal isc;
    @Column(name = "icbper", nullable = true)
    private BigDecimal icbper;
    @Column(name = "otros_cargos", nullable = true)
    private BigDecimal otrosCargos;
    @Column(name = "otros_tributos", nullable = true)
    private BigDecimal otrosTributos;
    @Column(name = "monto_redondeo", nullable = false)
    private BigDecimal montoRedondeo;
    @Column(name = "total", nullable = false)
    private BigDecimal total;
    @Column(name = "observacion", nullable = true)
    private String observacion;
    @Column(name = "usuario_modificador", nullable = false)
    private String usuarioModificador;
    @Column(name = "emisor_ruc", nullable = false)
    private String emisorRuc;

    @OneToMany(mappedBy = "facturaEntity", cascade = CascadeType.ALL)
    private List<OrderLineEntity> orderLineEntities = new ArrayList<>();

    @Column(name = "state", nullable = false)
    private Boolean state;

    @Column(name = "modified_by_user", nullable = false, length = 15)
    private String modifiedByUser;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;
}
