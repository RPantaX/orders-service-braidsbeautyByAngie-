package com.braidsbeautyByAngie.aggregates.response;

import com.braidsbeautyByAngie.aggregates.dto.AddressDTO;
import com.braidsbeautyByAngie.aggregates.dto.OrderLineDTO;
import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.aggregates.dto.ShoppingMethodDTO;

import java.util.List;

public class ResponseShopOrder {
    private ShopOrderDTO shopOrderDTO;
    private AddressDTO addressDTO;
    private List<OrderLineDTO> orderLineDTOList;
    private ShoppingMethodDTO shoppingMethodDTO;
    private Long factureNumber;
}
