package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShopOrderDTO {
    private Long shopOrderId;
    private Timestamp shopOrderDate;
    private Double shopOrderTotal;
    private String shopOrderStatus;
    private Long userId;
    private Long paymentMethodId;

}
