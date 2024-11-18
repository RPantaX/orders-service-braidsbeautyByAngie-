package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestShopOrder;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableShopOrder;

public interface ShopOrderServiceOut {
    void rejectShopOrderOut(Long orderId);
    void aprovedShopOrderOut(Long orderId);
    ShopOrderDTO createShopOrderOut(RequestShopOrder requestShopOrder);
    ResponseListPageableShopOrder getShopOrderListOut(int pageNumber, int pageSize, String orderBy, String sortDir);
}
