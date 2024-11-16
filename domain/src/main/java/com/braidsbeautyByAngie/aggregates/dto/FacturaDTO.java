package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FacturaDTO {
    private Long facturaNumero;

    private String facturaSerie;

    private String facturaSerienumero;

    private Timestamp fechaEmision;

    private BigDecimal subtotal;

    private BigDecimal igv;

    private BigDecimal descuentos;

    private BigDecimal anticipios;

    private BigDecimal valorVenta;

    private BigDecimal isc;

    private BigDecimal icbper;

    private BigDecimal otrosCargos;

    private BigDecimal otrosTributos;

    private BigDecimal montoRedondeo;

    private BigDecimal total;

    private String observacion;

    private String usuarioModificador;

    private String emisorRuc;
}
