package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShopOrderDTO {
    private Long shopOrderId;
    private LocalDate shopOrderDate;
    private Double shopOrderTotal;
    private String shopOrderStatus;
    private Long userId;
    private Long paymentMethodId;

}
