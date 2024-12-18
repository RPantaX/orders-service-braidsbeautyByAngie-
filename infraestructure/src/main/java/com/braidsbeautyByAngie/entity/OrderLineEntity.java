package com.braidsbeautyByAngie.entity;

import com.braidsbeautyByAngie.aggregates.types.OrderLineStatusEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_line")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_line_id", nullable = false)
    private Long orderLineId;
    @Column(name = "order_line_quantity", nullable = false)
    private int orderLineQuantity;
    @Column(name = "order_line_price", nullable = false)
    private double orderLinePrice;
    @Column(name = "order_line_total", nullable = false)
    private double orderLineTotal;
    @Column(name = "order_line_state", nullable = false)
    private OrderLineStatusEnum orderLineState;

    @Column(name="product_item_id", nullable = true)
    private Long productItemId;
    @Column(name="reservation_id", nullable = true)
    private Long reservationId;

    @Column(name="guia_remision_id", nullable = true)
    private Long guiaRemisionId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "shop_order_id", nullable = true)
    private ShopOrderEntity shopOrderEntity;

    @ManyToOne(optional = true)
    @JoinColumn(name = "factura_numero", nullable = true)
    private FacturaEntity facturaEntity;
}
