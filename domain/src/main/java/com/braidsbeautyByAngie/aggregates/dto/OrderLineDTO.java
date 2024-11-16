package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderLineDTO {
    private Long orderLineId;
    private int orderLineQuantity;
    private double orderLinePrice;
    private String orderLineState;

}
