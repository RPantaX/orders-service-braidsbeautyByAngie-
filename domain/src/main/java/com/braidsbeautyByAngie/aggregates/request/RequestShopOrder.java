package com.braidsbeautyByAngie.aggregates.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RequestShopOrder {
    private List<ProductRequest> productRequestList;
    private Long reservationId;
    private Long userId;
    private RequestAdress requestAdress;
    private Long shoppingMethodId;
}
