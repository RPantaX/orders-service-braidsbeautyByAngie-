package com.braidsbeautyByAngie.aggregates.response;

import com.braidsbeautyByAngie.aggregates.dto.AdressDTO;
import com.braidsbeautyByAngie.aggregates.dto.OrderLineDTO;
import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.aggregates.dto.ShoppingMethodDTO;

import java.util.List;

public class ResponseShopOrder {
    private ShopOrderDTO shopOrderDTO;
    private AdressDTO adressDTO;
    private List<OrderLineDTO> orderLineDTOList;
    private ShoppingMethodDTO shoppingMethodDTO;
    private Long factureNumber;
}
